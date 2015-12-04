package mx.cinvestav.gdl.iot.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import mx.cinvestav.gdl.iot.webpage.client.DatabaseException;
import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.sql.jdbc.Statement;
import com.mysql.jdbc.ResultSet;

public class DAO
{

	private static final String DEV_URL_ENDPOINT = "cloudsql.url.dev";
	private static final String PROD_URL_ENDPOINT = "cloudsql.url";
	private static final String GOOGLE_DRIVER = "com.mysql.jdbc.GoogleDriver";
	private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	private static final String JDBC_URL = "javax.persistence.jdbc.url";
	private static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";
	private static final String PERSISTENCE_UNIT_NAME = "SmartCitiesCloudSQL";

	private static EntityManagerFactory emf = null;

	/**
	 * Returns an entity manager instance
	 * @return
	 * @throws DatabaseException 
	 */
	public static synchronized EntityManager getEntityManager() throws DatabaseException
	{
		if (emf == null)
		{
			try
			{
				Map<String, String> db_props = new HashMap<String, String>();
				if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
				{
					db_props.put(JDBC_DRIVER, GOOGLE_DRIVER);
					db_props.put(JDBC_URL, System.getProperty(PROD_URL_ENDPOINT));
				}
				else
				{
					db_props.put(JDBC_DRIVER, MYSQL_DRIVER);
					db_props.put(JDBC_URL, System.getProperty(DEV_URL_ENDPOINT));
				}
				emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, db_props);
			}
			catch (Exception e)
			{
				Logger logger = Logger.getLogger(DAO.class.getName());
				logger.log(Level.SEVERE, "Unexpected exception initializing DAO", e);
				throw new DatabaseException("Exception creating entity manager", e);
			}
		}
		return emf.createEntityManager();
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	public static <T extends IoTEntity> List<T> getEntity(Class<T> entityClass, Integer id) throws DatabaseException
	{
		EntityManager em = null;
		List<T> resultList = null;
		try
		{
			em = getEntityManager();
			if (id == null)
			{
				CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
				cq.select(cq.from(entityClass));
				resultList = em.createQuery(cq).getResultList();
			}
			else
			{
				resultList = new ArrayList<>(1);
				T e = em.find(entityClass, id);
				resultList.add(e);
			}
			return resultList;
		}
		catch (Exception e)
		{
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	/**
	 * Insert a new controller with a collection of properties
	 * @param controller
	 * @param properties
	 * @throws DatabaseException
	 */
	public static <T extends IoTEntity> void insertEntity(T entity, Collection<? extends IoTProperty> properties)
			throws DatabaseException
	{
		if (entity == null)
		{
			throw new IllegalArgumentException("Entity cannot be null.");
		}
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			if (entity.getId() == null)
				em.persist(entity);
			else
				em.merge(entity);
			if (properties != null)
			{
				for (IoTProperty p : properties)
				{			
					p.setParentId(entity.getId());
					em.merge(p);
				}
			}
			//commit se utiliza para al macenar los cambios en disco
			
			tx.commit();
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static <T extends IoTProperty> List<T> getProperties(Class<T> propertyClass, Integer parentId)
			throws DatabaseException
	{
		EntityManager em = null;
		List<T> resultList = null;
		if (parentId == null)
		{
			throw new IllegalArgumentException("getProperties: must provide IoTEntity id.");
		}
		try
		{
			em = getEntityManager();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(propertyClass);
			Root<T> from = cq.from(propertyClass);
			ParameterExpression<Integer> parent = cb.parameter(Integer.class);
			cq.select(from).where(cb.equal(from.get(getParentRowName(propertyClass)), parent));
			
	
			TypedQuery<T> createQuery = em.createQuery(cq);
			createQuery.setParameter(parent, parentId);
			resultList = createQuery.getResultList();
			return resultList;
			
			
		}
		catch (Exception e)
		{
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static <T extends IoTProperty> void deleteProperty(Class<T> propertyClass, Integer id)
			throws DatabaseException
	{
		if (id == null)
		{
			throw new IllegalArgumentException("delete: must provide IoTProperty id.");
		}
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			T prop = em.find(propertyClass, id);
			em.remove(prop);
			tx.commit();
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw new DatabaseException("Database exception while deleting property:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static <T extends IoTEntity> void deleteEntity(Class<T> EntityClass, Integer id) throws DatabaseException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		if (id == null)
		{
			throw new IllegalArgumentException("delete: must provide IoTEntity id.");
		}
		try
		{
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			T prop = em.find(EntityClass, id);
			em.remove(prop);
			tx.commit();
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	private static String getParentRowName(Class<?> propertyClass)
	{
		if (ControllerProperty.class.equals(propertyClass)) return "idcontroller";
		if (SensorProperty.class.equals(propertyClass)) return "idsensor";
		if (SmartThingProperty.class.equals(propertyClass)) return "idthing";
		if (Controller.class.equals(propertyClass)) return "idcontroller";
		if (Sensor.class.equals(propertyClass)) return "idsensor";
		if (SmartThing.class.equals(propertyClass)) return "idthing";
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Measure> getSensorData(Integer idsensor, Integer idexperiment) throws DatabaseException
	{
		EntityManager em = null;
		if (idsensor == null)
		{
			throw new IllegalArgumentException("delete: must provide IoTEntity id.");
		}
		try
		{
			em = getEntityManager();
			String query = "SELECT * FROM data.data WHERE idsensor=? and idexperiment=? and charted=1 order by measure_date";
			Query q = em.createNativeQuery(query, Measure.class).setParameter(1, idsensor).setParameter(2, idexperiment);
            List<Measure> resultList = (List<Measure>) q.getResultList();
			return resultList;

		}
		catch (Exception e)
		{
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		

	finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}
	
/* 
Se generó un método  con el nombre getExperimentThing de tipo de dato entero, el cual contiene el parámetro idthing de tipo de dato Integer,
se le asignó la sentencia del try catch para que pueda cachar las posibles excepciones al método creado, 
cabe mencionar que el query se realizara desde la persistencia (EntityManager), ahora se le asigna a EntityManager una variable que en este caso es em, 
el cual em es igual a null, de la siguiente manera se le dice que si idthing es igual a null lance el nuevo argumento "delete: must provide IoTEntity id",
se declara la sentencia try catch y dentro de esta sentencia se realizó lo siguiente,  
se declaró que em es igual a getEntityManager y se dio paso a realizar el query,
lo cual se declara un tipo de dato String para el query en el cual se le asigna de que el query va ser igual al select que se le va hacer a la base de datos desde java.
El cuery es el siguiente "SELECT MAX (idexperiment) as idexperiment FROM experiment where idthing=?";  
la explicación del query es la siguiente:
Se le dice que seleccione el máximo idexperimento el cual en este caso seleccionara el ultimo idexperiment que encuentre 
en la base de datos, y va a realizar para la tabla experimento la búsqueda del idthing en este caso se deja abierta la búsqueda 
por que puede ser el idthing=3 o el idthing=4, a hora se le asigna que  Query q es igual a getEntityManager
y que cree el query y envié el parámetro 1 a idthing, el siguiente paso es: se debe realizar la conversión
para que q=query obtenga los registros de la lista, el cual se Inicializo el id a 0 y de tipo de dato int (entero), 
se le da la instrucción de que si es diferente (q) obtenga el resultado de la lista, 
ahora se realiza la conversión para que (q) obtenga el- resultado de la lista,  
se le agrega un sout para que realice  una impresión en consola para verificar si está entrando el valor,
si el valor está entrando retornara el id.
 */
	
	public static int getExperimentThing(Integer idthing) throws DatabaseException
	{
		EntityManager em = null;
		if (idthing == null)
		{
			throw new IllegalArgumentException("delete: must provide IoTEntity id.");
		}
		     try {
				    em = getEntityManager();
				    
				    String query = "SELECT MAX(idexperiment) as idexperiment FROM experiment where idthing=?";
					Query q = em.createNativeQuery(query).setParameter(1, idthing);
			
					/*conversion para que q=query obtenga los registros de la lista*/
					int id=0;/*Inicializo el id=0 de tipo de dato int*/
					if(!q.getResultList().isEmpty()){/*Le damos la instrucion de que si es diferente (q) obtenga el resultado de la lista*/
				    id=(int)q.getResultList().get(0);/* realizamos la conversion para que (q) obtenga el resultado de la lista */
				       
				    System.out.println("si entro el id"+id);/*Sout para imprimir en consola y saber si esta entrando el valor*/	    
					 }
				  else
						System.out.println("No entro el id");/*o de lo contrario no esta entrando*/
				    return id;
			
		     } 
		     catch (Exception e) 
		     {
					throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
				}
				finally
				{
					if (em != null)
					{
						em.close();
					}

				}
	         }

	public static User getUser(String username) throws DatabaseException
	{
		EntityManager em = null;
		if (username == null)
		{
			throw new IllegalArgumentException("getUser: must provide username.");
		}
		try
		{
			em = getEntityManager();
			TypedQuery<User> createQuery = em
					.createQuery("SELECT u FROM User u" + " WHERE u.username = ?1", User.class);
			createQuery.setParameter(1, username);
			List<User> resultList = createQuery.getResultList();
			if (resultList.size() >= 1) return resultList.get(0);
			return null;
		}
		catch (Exception e)
		{
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static void storeUser(User user) throws DatabaseException
	{
		if (user == null)
		{
			throw new IllegalArgumentException("User cannot be null.");
		}
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			if (user.getId() == null)
			{
				em.persist(user);
			}
			else
			{
				User stored = em.find(User.class, user.getId());
				if ("".equals(user.getHash())) user.setHash(stored.getHash());
				em.merge(user);
			}
			tx.commit();
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static List<User> getUser(Integer id) throws DatabaseException
	{
		EntityManager em = null;
		try
		{
			em = getEntityManager();
			List<User> resultList = null;

			if (id == null)
			{
				CriteriaQuery<User> cq = em.getCriteriaBuilder().createQuery(User.class);
				cq.select(cq.from(User.class));
				resultList = em.createQuery(cq).getResultList();
			}
			else
			{
				resultList = new ArrayList<>(1);
				User e = em.find(User.class, id);
				resultList.add(e);
			}

			return resultList;
		}
		catch (Exception e)
		{
			throw new DatabaseException("Database exception while retrieving user:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static void deleteUser(Integer id) throws DatabaseException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		if (id == null)
		{
			throw new IllegalArgumentException("delete: must provide user id.");
		}
		try
		{
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			User user = em.find(User.class, id);
			em.remove(user);
			tx.commit();
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw new DatabaseException("Database exception while inserting entity:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}

	}

	public static void insertSensorType(SensorType entity) throws DatabaseException
	{
		if (entity == null || "".equals(entity.getName()))
		{
			throw new IllegalArgumentException("Sensor type cannot be null/empty.");
		}
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			if (entity.getId() == null)
				em.persist(entity);
			else
				em.merge(entity);
			tx.commit();
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw new DatabaseException("Database exception while inserting sensor type:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}

	public static List<SensorType> getSensorTypeList() throws DatabaseException
	{
		EntityManager em = null;
		List<SensorType> resultList = null;
		try
		{
			em = getEntityManager();
			CriteriaQuery<SensorType> cq = em.getCriteriaBuilder().createQuery(SensorType.class);
			cq.select(cq.from(SensorType.class));
			resultList = em.createQuery(cq).getResultList();
			return resultList;
		}
		catch (Exception e)
		{
			throw new DatabaseException("Database exception while geetting sensor type:" + e.getMessage(), e);
		}
		finally
		{
			if (em != null)
			{
				em.close();
			}
		}
	}
}
