package EJB.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"__EFMigrationsHistory\"")
public class Efmigrationshistory {
    @Id
    @Column(name = "\"MigrationId\"", nullable = false, length = 150)
    private String migrationId;

    @Column(name = "\"ProductVersion\"", nullable = false, length = 32)
    private String productVersion;

    public String getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

}