package tests.io.github.zeshan.hqlsniffer.entities;

import javax.persistence.Id;public class GetterIdentifierEntity {

    private String id;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}