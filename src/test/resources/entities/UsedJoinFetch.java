package io.github.zeshan.ORMStyle.example.domain.fig1;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;public class UsedJoinFetch {

    public List<ManyToOneEagerEntity> findStudentsJoinFetch(Integer id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("tests.io.github.zeshan.hqlsniffer.entities.ManyToOneEagerEntity");
        String hql = " FROM ManyToOneEagerEntity d";
        hql.append ("JOIN FETCH d.parent p ");
        hql.append ("WHERE name = : name ");

        Query q = emf.createEntityManager().createQuery(hql);
        return (List<ManyToOneEagerEntity>) q.getResultList();
    }

    public List<ManyToOneEagerEntity> students() {
        findStudentsJoinFetch(200);
    }

}
