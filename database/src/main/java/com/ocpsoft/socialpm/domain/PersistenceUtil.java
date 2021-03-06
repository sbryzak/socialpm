/*
 * Copyright 2010 - Lincoln Baxter, III (lincoln@ocpsoft.com) - Licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at http://www.apache.org/licenses/LICENSE-2.0 - Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.ocpsoft.socialpm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

public abstract class PersistenceUtil implements Serializable
{
   private static final long serialVersionUID = -276417828563635020L;

   protected abstract EntityManager getEntityManager();

   protected <T> void create(final T entity)
   {
      getEntityManager().persist(entity);
   }

   protected <T> void delete(final T entity) throws NoSuchObjectException
   {
      try
      {
         getEntityManager().remove(entity);
      }
      catch (NoResultException e)
      {
         throw new NoSuchObjectException();
      }
   }

   protected <T> T deleteById(final Class<T> type, final Long id) throws NoSuchObjectException
   {
      T object = findById(type, id);
      delete(object);
      return object;
   }

   @SuppressWarnings("unchecked")
   protected <T> T findById(final Class<T> type, final Long id) throws NoSuchObjectException
   {
      Class<?> clazz = getObjectClass(type);
      T result = (T) getEntityManager().find(clazz, id);
      if (result == null)
      {
         throw new NoSuchObjectException("No object of type: " + type + " with ID: " + id);
      }
      return result;
   }

   protected <T> void save(final T entity)
   {
      getEntityManager().merge(entity);
   }

   protected <T> void refresh(final T entity)
   {
      getEntityManager().refresh(entity);
   }

   protected Class<?> getObjectClass(final Object type) throws IllegalArgumentException
   {
      Class<?> clazz = null;
      if (type == null)
      {
         throw new IllegalArgumentException("Null has no type. You must pass an Object");
      }
      else if (type instanceof Class<?>)
      {
         clazz = (Class<?>) type;
      }
      else
      {
         clazz = type.getClass();
      }
      return clazz;
   }

   @SuppressWarnings("unchecked")
   protected <T> List<T> findByNamedQuery(final String namedQueryName)
   {
      return getEntityManager().createNamedQuery(namedQueryName).getResultList();
   }

   @SuppressWarnings("unchecked")
   protected <T> List<T> findByNamedQuery(final String namedQueryName, final Object... params)
   {
      Query query = getEntityManager().createNamedQuery(namedQueryName);
      int i = 1;
      for (Object p : params)
      {
         query.setParameter(i++, p);
      }
      return query.getResultList();
   }

   protected <T> List<T> findAll(final Class<T> type)
   {
      CriteriaQuery<T> query = getEntityManager().getCriteriaBuilder().createQuery(type);
      query.from(type);
      return getEntityManager().createQuery(query).getResultList();
   }

   @SuppressWarnings("unchecked")
   protected <T> T findUniqueByNamedQuery(final String namedQueryName) throws NoSuchObjectException
   {
      try
      {
         return (T) getEntityManager().createNamedQuery(namedQueryName).getSingleResult();
      }
      catch (NoResultException e)
      {
         throw new NoSuchObjectException(e);
      }
   }

   @SuppressWarnings("unchecked")
   protected <T> T findUniqueByNamedQuery(final String namedQueryName, final Object... params) throws NoSuchObjectException
   {
      Query query = getEntityManager().createNamedQuery(namedQueryName);
      int i = 1;
      for (Object p : params)
      {
         query.setParameter(i++, p);
      }

      try
      {
         return (T) query.getSingleResult();
      }
      catch (NoResultException e)
      {
         throw new NoSuchObjectException(e);
      }
   }
}