public class KrbPrincipal {

    private KrbData[] components;
    private KrbData realm;

    public KrbData[] getComponents() { return components; }
    public void setComponents(KrbData[] components) { this.components = components; }

    public KrbData getRealm() { return realm; }
    public void setRealm(KrbData realm) { this.realm = realm; }
}
