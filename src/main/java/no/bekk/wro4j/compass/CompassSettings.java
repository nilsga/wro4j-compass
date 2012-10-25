package no.bekk.wro4j.compass;

import ro.isdc.wro.manager.factory.standalone.StandaloneContext;

import java.io.File;

public class CompassSettings {
    private StandaloneContext standaloneContext;
    private String compassBaseDir;
    private File projectBaseDir;
    private String gemHome;

    public String getCompassBaseDir() {
        return compassBaseDir;
    }

    public void setCompassBaseDir(String compassBaseDir) {
        this.compassBaseDir = compassBaseDir;
    }

    public String getGemHome() {
        return gemHome;
    }

    public void setGemHome(String gemHome) {
        this.gemHome = gemHome;
    }

    public File getProjectBaseDir() {
        return projectBaseDir;
    }

    public void setProjectBaseDir(File projectBaseDir) {
        this.projectBaseDir = projectBaseDir;
    }

    public StandaloneContext getStandaloneContext() {
        return standaloneContext;
    }

    public void setStandaloneContext(StandaloneContext standaloneContext) {
        this.standaloneContext = standaloneContext;
    }
}
