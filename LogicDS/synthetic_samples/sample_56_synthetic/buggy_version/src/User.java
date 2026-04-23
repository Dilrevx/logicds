public class User {

    public int id;
    public boolean canEditShelfs;
    public boolean koboOnlyShelvesSync;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean roleEditShelfs() { return canEditShelfs; }
    public void setCanEditShelfs(boolean canEditShelfs) { this.canEditShelfs = canEditShelfs; }

    public boolean isKoboOnlyShelvesSync() { return koboOnlyShelvesSync; }
    public void setKoboOnlyShelvesSync(boolean v) { this.koboOnlyShelvesSync = v; }
}
