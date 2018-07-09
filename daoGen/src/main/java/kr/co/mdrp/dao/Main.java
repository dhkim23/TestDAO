package kr.co.mdrp.dao;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

public class Main {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "kr.co.mdrp.testdao.dao");// 객체가만들어질 위치

        Entity person = schema.addEntity("Person"); // Person 테이블추가
        person.addIdProperty(); // ID에대한 PK 값
        person.addStringProperty("name"); //name에대한 String 필드
        person.addStringProperty("comment");//Comment 에대한 String 필드

        Entity lease = schema.addEntity("Lease");
        lease.addIdProperty();
        lease.addStringProperty("item");
        lease.addStringProperty("comment");
        lease.addLongProperty("leasedate");
        lease.addLongProperty("returndate");

/**
 * In your greenDAO generator model you must model a property for the foreign key (ID) value. Using t           his property, you can add a to-one relation using Entity.addToOne.
 */

        Property personId = lease.addLongProperty("personId").getProperty();
        lease.addToOne(person, personId);

//foreign key (To-Many Relations)
        ToMany personToLease = person.addToMany(lease, personId);
        personToLease.setName("leases");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }


}
