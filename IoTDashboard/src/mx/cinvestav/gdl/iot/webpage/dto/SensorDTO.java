package mx.cinvestav.gdl.iot.webpage.dto;

import java.util.Map;

public class SensorDTO implements IoTEntityDTO
{
	private static final long serialVersionUID = -1165471382042735357L;

	private Integer id;
	private Boolean active;
	private String name;
	private String description;
	private String sensor_type;
	private String unit;
	private double latitude;
	private double longitude;
	private double altitude;
	private Map<Integer, MeasureDTO> measures;
	private Map<Integer, SensorPropertyDTO> properties;
	private Integer idthing;

	public SensorDTO()
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

	public Map<Integer, MeasureDTO> getMeasures()
	{
		return measures;
	}

	public void setMeasures(Map<Integer, MeasureDTO> measures)
	{
		this.measures = measures;
	}

	public Map<Integer, SensorPropertyDTO> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<Integer, SensorPropertyDTO> properties)
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
