package tests.io.github.zeshan.hqlsniffer.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public final class DefaultNoArgConstructorFinalEntity {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}