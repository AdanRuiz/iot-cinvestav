package mx.cinvestav.gdl.iot.dao;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "sensor")
public class Sensor implements IoTEntity
{
	
	//serialVersionUID:Es un n�mero de versi�n que posee cada clase Serializable, el cual es usado en la deserializaci�n 
	//para verificar que el emisor y el receptor de un objeto serializado mantienen una compatibilidad 
	//en lo que a serializaci�n se refiere con respecto a la clases que tienen cargadas (el emisor y el receptor).
	
	private static final long serialVersionUID = -1165471382042735357L;

	@Id
	@GeneratedValue
	@Column(name = "idsensor")
	private Integer id;

	@Column(name = "isactive")
	private Boolean active;

	private String name;
	private String description;
	private String sensor_type;
	private String unit;

	private double latitude;
	private double longitude;
	private double altitude;
	@Transient
	transient private Map<Integer, Measure> measures;
	
	//transient sirven para demarcar el car�cter temporal o transitorio de dicha variable,
	//es decir, que no siempre se tendr� acceso al valor de la variable.
	@Transient
	transient private Map<Integer, SensorProperty> properties;
	private Integer idthing;

	public Sensor()
	{
		super();
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Boolean isActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getSensor_type()
	{
		return sensor_type;
	}

	public void setSensor_type(String sensor_type)
	{
		this.sensor_type = sensor_type;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getAltitude()
	{
		return altitude;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}
//mapeo para mediciones
	public Map<Integer, Measure> getMeasures()
	{
		return measures;
	}

	public void setMeasures(Map<Integer, Measure> measures)
	{
		this.measures = measures;
	}
//mapeo para la propiedad del sensor
	public Map<Integer, SensorProperty> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<Integer, SensorProperty> properties)
	{
		this.properties = properties;
	}

	public Integer getIdthing()
	{
		return idthing;
	}

	public void setIdthing(Integer idthing)
	{
		this.idthing = idthing;
	}
}
