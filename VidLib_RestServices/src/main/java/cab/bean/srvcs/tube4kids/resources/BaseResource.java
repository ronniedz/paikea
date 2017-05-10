package cab.bean.srvcs.tube4kids.resources;

import javax.ws.rs.core.Context;

public class BaseResource extends ResourceStandards {
    protected static final String SPLIT_PARAM_REGEX = "[,\\|\\;\\s]+";

    @Context
    protected javax.ws.rs.container.ResourceContext rc;
}
