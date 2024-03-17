package tests.io.github.zeshan.hqlsniffer.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "Dummy")
public class ListCollectionEntity {

    @OneToMany
    public List<NoArgConstructorEntity> lst;
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}