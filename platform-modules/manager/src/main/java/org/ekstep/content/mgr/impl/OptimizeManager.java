package org.ekstep.content.mgr.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.ekstep.common.dto.Response;
import org.ekstep.common.exception.ClientException;
import org.ekstep.common.exception.ResponseCode;
import org.ekstep.common.optimizr.Optimizr;
import org.ekstep.graph.dac.model.Node;
import org.ekstep.learning.common.enums.ContentAPIParams;
import org.ekstep.learning.common.enums.ContentErrorCodes;
import org.ekstep.learning.util.CloudStore;
import org.ekstep.taxonomy.mgr.impl.DummyBaseContentManager;
import org.ekstep.telemetry.logger.TelemetryManager;

import java.io.File;

public class OptimizeManager extends DummyBaseContentManager {

    public Response optimize(String contentId) {

        if (StringUtils.isBlank(contentId))
            throw new ClientException(ContentErrorCodes.ERR_CONTENT_BLANK_ID.name(), "Content Id is blank");

        Response response = new Response();
        Node node = getNodeForOperation(contentId, "optimize");

        isNodeUnderProcessing(node, "Optimize");

        String status = (String) node.getMetadata().get(ContentAPIParams.status.name());
        TelemetryManager.log("Content Status: " + status);
        if (!StringUtils.equalsIgnoreCase(ContentAPIParams.Live.name(), status)
                || !StringUtils.equalsIgnoreCase(ContentAPIParams.Unlisted.name(), status))
            throw new ClientException(ContentErrorCodes.ERR_CONTENT_OPTIMIZE.name(),
                    "UnPublished content cannot be optimized");

        String downloadUrl = (String) node.getMetadata().get(ContentAPIParams.downloadUrl.name());
        TelemetryManager.log("Download Url: " + downloadUrl);
        if (StringUtils.isBlank(downloadUrl))
            throw new ClientException(ContentErrorCodes.ERR_CONTENT_OPTIMIZE.name(),
                    "ECAR file not available for content");

        if (!StringUtils.equalsIgnoreCase(ContentAPIParams.ecar.name(), FilenameUtils.getExtension(downloadUrl)))
            throw new ClientException(ContentErrorCodes.ERR_CONTENT_OPTIMIZE.name(),
                    "Content package is not an ECAR file");

        String optStatus = (String) node.getMetadata().get(ContentAPIParams.optStatus.name());
        TelemetryManager.log("Optimization Process Status: " + optStatus);
        if (StringUtils.equalsIgnoreCase(ContentAPIParams.Processing.name(), optStatus))
            throw new ClientException(ContentErrorCodes.ERR_CONTENT_OPTIMIZE.name(),
                    "Content optimization is in progress. Please try after the current optimization is complete");

        node.getMetadata().put(ContentAPIParams.optStatus.name(), ContentAPIParams.Processing.name());
        updateDataNode(node);
        Optimizr optimizr = new Optimizr();
        try {
            TelemetryManager.log("Invoking the Optimizer for Content Id: " + contentId);
            File minEcar = optimizr.optimizeECAR(downloadUrl);
            TelemetryManager.log("Optimized File: " + minEcar.getName() + " | [Content Id: " + contentId + "]");

            String folder = getFolderName(downloadUrl);
            TelemetryManager.log("Folder Name: " + folder + " | [Content Id: " + contentId + "]");

            //String[] arr = AWSUploader.uploadFile(folder, minEcar);
            String[] arr = CloudStore.uploadFile(folder, minEcar, true);
            response.put("url", arr[1]);
            TelemetryManager.log("URL: " + arr[1] + " | [Content Id: " + contentId + "]");

            TelemetryManager.log("Updating the Optimization Status. | [Content Id: " + contentId + "]");
            node.getMetadata().put(ContentAPIParams.optStatus.name(), "Complete");
            updateDataNode(node);
            TelemetryManager.log("Node Updated. | [Content Id: " + contentId + "]");

            TelemetryManager.log("Directory Cleanup. | [Content Id: " + contentId + "]");
            FileUtils.deleteDirectory(minEcar.getParentFile());
        } catch (Exception e) {
            node.getMetadata().put(ContentAPIParams.optStatus.name(), "Error");
            updateDataNode(node);
            response = ERROR(ContentErrorCodes.ERR_CONTENT_OPTIMIZE.name(), e.getMessage(), ResponseCode.SERVER_ERROR);
        }
        return response;
    }

}
