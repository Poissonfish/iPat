import java.util.ArrayList;

public class Command extends ArrayList<String> {
    String wd;
    String project;
    String cov;
    MethodType type;

    public Command() {
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

    void setMethod(MethodType type) {
        this.type = type;
    }

    Command getCopy() {
        Command command = new Command();
        if (!this.isEmpty()) {
            command.addAll(this);
            command.addWD(this.getWD());
            command.addProject(this.getProject());
            command.addCov(this.getCov());
        }
        return command;
    }

    MethodType getType() {
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
