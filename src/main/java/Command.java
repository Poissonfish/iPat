import java.util.ArrayList;

public class Command extends ArrayList<String> {
    String wd;
    String project;

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

    String[] getCommand() {
        return this.toArray(new String[0]);
    }
    String getProject() {
        return this.project;
    }
    String getWD() {
        return this.wd;
    }
}
