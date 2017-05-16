package cab.bean.srvcs.tube4kids.views;

import cab.bean.srvcs.tube4kids.core.Child;

import io.dropwizard.views.View;

public class ChildView extends View {
    private final Child child;

    public enum Template {
        FREEMARKER("freemarker/child.ftl"),
        MUSTACHE("mustache/child.mustache");

        private String templateName;

        Template(String templateName) {
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    public ChildView(ChildView.Template template, Child child) {
        super(template.getTemplateName());
        this.child = child;
    }

    public Child getChild() {
        return child;
    }
}
