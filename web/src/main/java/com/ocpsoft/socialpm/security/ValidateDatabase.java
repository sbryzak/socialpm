package com.ocpsoft.socialpm.security;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.security.management.picketlink.IdentitySessionProducer;
import org.jboss.seam.servlet.WebApplication;
import org.jboss.seam.servlet.event.Initialized;
import org.jboss.seam.transaction.Transactional;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityException;

import com.ocpsoft.socialpm.domain.security.IdentityObjectCredentialType;
import com.ocpsoft.socialpm.domain.security.IdentityObjectType;

/**
 * Validates that the database contains the minimum required entities to function
 * 
 * @author Shane Bryzak
 */
public class ValidateDatabase
{
   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private IdentitySessionFactory identitySessionFactory;

   @Transactional
   public void validate(@Observes @Initialized final WebApplication webapp) throws IdentityException
   {
      validateIdentityObjectTypes();
      validateSecurity();
   }

   private void validateIdentityObjectTypes()
   {
      if (entityManager.createQuery("select t from IdentityObjectType t where t.name = :name")
               .setParameter("name", "USER")
               .getResultList().size() == 0) {

         IdentityObjectType user = new IdentityObjectType();
         user.setName("USER");
         entityManager.persist(user);
      }

      if (entityManager.createQuery("select t from IdentityObjectType t where t.name = :name")
               .setParameter("name", "GROUP")
               .getResultList().size() == 0) {

         IdentityObjectType group = new IdentityObjectType();
         group.setName("GROUP");
         entityManager.persist(group);
      }
   }

   private void validateSecurity() throws IdentityException
   {
      // Validate credential types
      if (entityManager.createQuery("select t from IdentityObjectCredentialType t where t.name = :name")
               .setParameter("name", "PASSWORD")
               .getResultList().size() == 0) {

         IdentityObjectCredentialType PASSWORD = new IdentityObjectCredentialType();
         PASSWORD.setName("PASSWORD");
         entityManager.persist(PASSWORD);
      }

      Map<String, Object> sessionOptions = new HashMap<String, Object>();
      sessionOptions.put(IdentitySessionProducer.SESSION_OPTION_ENTITY_MANAGER, entityManager);

      IdentitySession session = identitySessionFactory.createIdentitySession("default", sessionOptions);

      if (session.getPersistenceManager().findUser("shane") == null) {
         User u = session.getPersistenceManager().createUser("shane");
         session.getAttributesManager().updatePassword(u, "password");
      }
   }
}
