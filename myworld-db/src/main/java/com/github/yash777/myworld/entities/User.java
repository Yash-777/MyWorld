package com.github.yash777.myworld.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity @Table(name = "User")
//@lombok.Data // Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.
//@lombok.Setter @lombok.Getter
//org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'userRepository' defined in com.yash.db.repo.UserRepository defined in @EnableJpaRepositories declared on DataSourceConfig: Did not find a query class com.yash.db.entities.QUser for domain class com.yash.db.entities.User
public class User extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

/*
* ON: hibernate.hbm2ddl.auto = update

WARNING: org.hibernate.tool.schema.spi.CommandAcceptanceException: Error executing DDL "alter table User modify column first_name varchar(7) not null" via JDBC [Data truncation: Data too long for column 'first_name' at row 1]
	Caused by: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column 'first_name' at row 1
	
Existing Database Column (first_name): The first_name column in the User table is currently defined as VARCHAR(255), which means it can hold up to 255 characters.

JPA Annotation: The @Column(name = "first_name", nullable = false, length = 7) annotation specifies that Hibernate should expect the first_name column to be VARCHAR(7), i.e., it should only hold up to 7 characters.

The Alter Table Command: Hibernate is attempting to modify the column to VARCHAR(7) with the DDL command:
alter table User modify column first_name varchar(7) not null

However, this results in a data truncation error because there may already be data in the first_name column that exceeds 7 characters. Since the column is being modified to only allow 7 characters, any data that is longer than 7 characters will cause this error.

@Column(name = "first_name", nullable = false, length = 7) //IN DB `first_name` varchar(255) NOT NULL,
private String firstName;

> Check Data Lengths
You need to check the data in the first_name column to ensure that no data exceeds 7 characters. If there are any rows with first_name values longer than 7 characters, you will need to either truncate or update them before running the migration.

SELECT first_name FROM User WHERE LENGTH(Code) > 7; // Yash Some Last name and Some middle name

Update the values to fit within the 7-character limit:
-- Truncate data that exceeds 7 characters
UPDATE valuechainconfigtype SET Code = LEFT(Code, 7) WHERE LENGTH(Code) > 7;

-- Alter column
ALTER TABLE valuechainconfigtype MODIFY COLUMN Code VARCHAR(7) NOT NULL;
*
*/
	@Column(name = "first_name", nullable = false, length = 7) //`first_name` varchar(255) NOT NULL,
	private String firstName;
	
	@Column(name = "Gender")
	private Byte gender;
	
	@Column(name = "EmailId",nullable = false)
	private String emailId;
	
	//@org.hibernate.annotations.Type(type = "text")
	@Column(name = "Definition",nullable = false, columnDefinition = "text")
	private String definition;
	
//	@javax.persistence.OneToMany
//	@javax.persistence.JoinColumn(name="address_Id")
//	private List<Address> address;
	

/*
* Branch Office is common to some the users, so use ManyToMany

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'rdbmsEntityManager' defined in class path resource [com/yash/db/config/DataSourceConfig.class]: Property 'com.yash.db.entities.User.address' is a '@ManyToMany' and is directly annotated '@JoinColumn' (specify '@JoinColumn' inside '@JoinTable' or '@CollectionTable')

RootCause of issue = Property 'com.yash.db.entities.User.address' is a '@ManyToMany' and is directly annotated '@JoinColumn' (specify '@JoinColumn' inside '@JoinTable' or '@CollectionTable')

Explanation of the Error
@ManyToMany Relationship:

The @ManyToMany annotation is used to indicate that each entity instance is related to multiple instances of another entity.
In this case, it looks like User has a ManyToMany relationship with Address.
@JoinColumn Misuse:

@JoinColumn is used to define the column that will serve as the join between two tables. However, @ManyToMany relationships should not directly use @JoinColumn.
Instead, @JoinTable is required for @ManyToMany relationships, which specifies the join table that contains the mapping between the two entities.
*
*/
	//@OneToMany
//	@ManyToMany // jakarta/javax.persistence.ManyToMany @JoinTable @JoinColumn
//	//@JoinColumn(name="address_Id")
//	@JoinTable( // @ManyToMany relationship with Address
//			name = "user_address", // the name of the join table
//			joinColumns = @JoinColumn(name = "user_id"), // the join column for this entity
//			inverseJoinColumns = @JoinColumn(name = "address_id") // the join column for the Address entity
//		)
//	//@LazyCollection(LazyCollectionOption.FALSE) // The type LazyCollection is deprecated since version 6.2 -> <hibernate.version>6.5.2.Final</hibernate.version>
//	private List<Address> address;
	
//	@ManyToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name ="Address_Id")
//	private Address address;
	
/*
Caused by: jakarta.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: 
Schema-validation: wrong column type encountered in column [activity_end_date] in table [User]; found [date (Types#DATE)], but expecting [datetime(6) (Types#TIMESTAMP)]
ALTER table User add column activity_end_date date;

Timestamp - DATE         in table [User]; found [date (Types#DATE)], but expecting [datetime(6) (Types#TIMESTAMP)]
Timestamp - TIMESTAMP
Timestamp - datetime(6)

Date - DATE
*/
	@Column(name = "activity_start_date") @CreationTimestamp
	private Timestamp activityStartDate;

	@Column(name = "activity_end_date")
	private Timestamp activityDate;

}
/*
* 
import javax.annotation.processing.Generated;
import com.querydsl.core.types.dsl.EntityPathBase;

@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {
*/