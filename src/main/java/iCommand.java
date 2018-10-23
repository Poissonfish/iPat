import java.util.ArrayList;

public class iCommand extends ArrayList<String> {
    String wd;
    String project;
    String cov;
    Enum_Analysis type;

    public iCommand() {
        super();
    }

    void addProject(String newProject) {
        this.project = newProject;
        this.add("-project");
        this.add(newProject);
    }

    void addWD(String newWD) {
        this.wd = newWD;
        this.add("-wd");
        this.add(newWD);
    }

    void addArg(String tag, String arg) {
        this.add(tag);
        this.add(arg);
    }

    void addCov(String selectC) {
        this.cov = selectC;
        this.add("-cSelect");
        this.add(selectC);
    }

    void setMethod(Enum_Analysis type) {
        this.type = type;
    }

    iCommand getCopy() {
        iCommand command = new iCommand();
        if (!this.isEmpty()) {
            command.addAll(this);
            command.addWD(this.getWD());
            command.addProject(this.getProject());
            command.addCov(this.getCov());
        }
        return command;
    }

    Enum_Analysis getType() {
        return this.type;
    }

    String[] getCommand() {
        return this.toArray(new String[0]);
    }
    String getProject() {
        return this.project;
    }
    String getWD() {
        return this.wd;
    }
    String getCov() {
        return this.cov;
    }
}
