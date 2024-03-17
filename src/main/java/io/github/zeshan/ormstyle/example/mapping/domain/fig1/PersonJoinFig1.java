package io.github.zeshan.ORMStyle.example.mapping.domain.fig1;


import io.github.zeshan.ORMStyle.example.mapping.domain.Person;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "person_fig1")
public class PersonJoinFig1 implements Person {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    public Integer id;

    @Column(name = "name")
    public String name;
}