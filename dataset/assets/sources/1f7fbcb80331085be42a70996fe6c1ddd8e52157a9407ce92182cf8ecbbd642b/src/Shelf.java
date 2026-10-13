public class Shelf {

    public Integer id;
    public String name;
    public int userId;
    public int isPublic;
    public boolean koboSync;
    public String uuid;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getIsPublic() { return isPublic; }
    public void setIsPublic(int isPublic) { this.isPublic = isPublic; }

    public boolean isKoboSync() { return koboSync; }
    public void setKoboSync(boolean koboSync) { this.koboSync = koboSync; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
}
