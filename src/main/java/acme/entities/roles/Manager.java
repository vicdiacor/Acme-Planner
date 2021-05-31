/*
 * Consumer.java
 *
 * Copyright (C) 2012-2021 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.entities.roles;

import javax.persistence.Entity;

import acme.framework.entities.UserRole;

@Entity
//@Getter
//@Setter
public class Manager extends UserRole {

	
	protected static final long	serialVersionUID	= 1L;

	
}
