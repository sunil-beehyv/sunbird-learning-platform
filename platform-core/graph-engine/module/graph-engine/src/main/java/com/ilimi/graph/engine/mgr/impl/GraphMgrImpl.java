package com.ilimi.graph.engine.mgr.impl;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;

import com.ilimi.common.dto.Request;
import com.ilimi.common.exception.ClientException;
import com.ilimi.graph.common.enums.GraphEngineParams;
import com.ilimi.graph.common.enums.GraphHeaderParams;
import com.ilimi.graph.common.mgr.BaseGraphManager;
import com.ilimi.graph.dac.enums.GraphDACParams;
import com.ilimi.graph.engine.mgr.IGraphManager;
import com.ilimi.graph.engine.router.GraphEngineActorPoolMgr;
import com.ilimi.graph.engine.router.GraphEngineManagers;
import com.ilimi.graph.exception.GraphEngineErrorCodes;
import com.ilimi.graph.exception.GraphRelationErrorCodes;
import com.ilimi.graph.model.Graph;
import com.ilimi.graph.model.IRelation;
import com.ilimi.graph.model.relation.RelationHandler;

public class GraphMgrImpl extends BaseGraphManager implements IGraphManager {

    private static final Logger logger = LogManager.getLogger(IGraphManager.class.getName());

    protected void invokeMethod(Request request, ActorRef parent) {
        String methodName = request.getOperation();
        try {
            Method method = GraphEngineActorPoolMgr.getMethod(GraphEngineManagers.GRAPH_MANAGER, methodName);
            if (null == method) {
                throw new ClientException("ERR_GRAPH_INVALID_OPERATION", "Operation '" + methodName + "' not found");
            } else {
                method.invoke(this, request);
            }
        } catch (Exception e) {
            ERROR(e, parent);
        }
    }

    @Override
    public void createGraph(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        try {
            logger.info("Create Graph request: " + graphId);
            Graph graph = new Graph(this, graphId);
            graph.create(request);
        } catch (Exception e) {
            logger.error("Error in Create Graph", e);
            handleException(e, getSender());
        }
    }

    @Override
    public void loadGraph(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        try {
            Graph graph = new Graph(this, graphId);
            graph.load(request);
        } catch (Exception e) {
            handleException(e, getSender());
        }
    }

    @Override
    public void validateGraph(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        try {
            Graph graph = new Graph(this, graphId);
            graph.validate(request);
        } catch (Exception e) {
            handleException(e, getSender());
        }
    }

    @Override
    public void deleteGraph(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        try {
            Graph graph = new Graph(this, graphId);
            graph.delete(request);
        } catch (Exception e) {
            handleException(e, getSender());
        }
    }

    @Override
    public void importGraph(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        try {
            Graph graph = new Graph(this, graphId);
            graph.importGraph(request);
        } catch (Exception e) {
            handleException(e, getSender());
        }
    }

    @Override
    public void exportGraph(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        String format = (String) request.get(GraphEngineParams.format.name());
        if (!validateRequired(format)) {
            throw new ClientException(GraphEngineErrorCodes.ERR_GRAPH_EXPORT_INVALID_FORMAT.name(), "Required parameters are missing...");
        } else {
            try {
                Graph graph = new Graph(this, graphId);
                graph.exportGraph(request);
            } catch (Exception e) {
                handleException(e, getSender());
            }
        }
    }

    @Override
    public void createRelation(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        String startNodeId = (String) request.get(GraphDACParams.start_node_id.name());
        String relationType = (String) request.get(GraphDACParams.relation_type.name());
        String endNodeId = (String) request.get(GraphDACParams.end_node_id.name());
        if (!validateRequired(startNodeId, relationType, endNodeId)) {
            throw new ClientException(GraphRelationErrorCodes.ERR_RELATION_CREATE.name(), "Required parameters are missing...");
        } else {
            try {
                IRelation relation = RelationHandler.getRelation(this, graphId, startNodeId, relationType, endNodeId);
                relation.create(request);
            } catch (Exception e) {
                handleException(e, getSender());
            }
        }
    }

    @Override
    public void removeRelation(Request request) {
        String graphId = (String) request.getContext().get(GraphHeaderParams.graph_id.name());
        String startNodeId = (String) request.get(GraphDACParams.start_node_id.name());
        String relationType = (String) request.get(GraphDACParams.relation_type.name());
        String endNodeId = (String) request.get(GraphDACParams.end_node_id.name());
        if (!validateRequired(startNodeId, relationType, endNodeId)) {
            throw new ClientException(GraphRelationErrorCodes.ERR_RELATION_CREATE.name(), "Required parameters are missing...");
        } else {
            try {
                IRelation relation = RelationHandler.getRelation(this, graphId, startNodeId, relationType, endNodeId);
                relation.delete(request);
            } catch (Exception e) {
                handleException(e, getSender());
            }
        }
    }

}
