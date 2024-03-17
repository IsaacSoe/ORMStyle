


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Entity2 {

    private String id;

    @Id
    public String getId() {
        return id;
    }
}