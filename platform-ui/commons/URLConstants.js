exports.GET_TAXONOMIES = '/taxonomy-service/taxonomy';
exports.GET_TAXONOMY = '/taxonomy-service/taxonomy/${id}';
exports.GET_CONCEPT_TAXONOMY_DEFS = '/taxonomy-service/taxonomy/${id}/definition/Concept';
exports.GET_GAME_TAXONOMY_DEFS = '/taxonomy-service/taxonomy/${id}/definition/Game';
exports.GET_CONCEPT = '/taxonomy-service/concept/${id}';
exports.GET_CONCEPTS = '/taxonomy-service/concept';
exports.SAVE_CONCEPT = '/taxonomy-service/concept?taxonomyId=${tid}';
exports.UPDATE_CONCEPT = '/taxonomy-service/concept/${id}?taxonomyId=${tid}';
exports.GET_GAMES = '/taxonomy-service/learning-object';
exports.GET_GAME = '/taxonomy-service/learning-object/${id}';
exports.SAVE_GAME = '/taxonomy-service/learning-object?taxonomyId=${tid}';
exports.UPDATE_GAME = '/taxonomy-service/learning-object/${id}?taxonomyId=${tid}';
exports.AUDIT_HISTORY = '/taxonomy-service/audithistory/${id}';
exports.GET_COMMENTS = '/taxonomy-service/comment/${id}';
exports.SAVE_COMMENT = '/taxonomy-service/comment?taxonomyId=${tid}';
exports.GET_COMMENT_THREAD = '/taxonomy-service/comment/${id}/${threadId}';