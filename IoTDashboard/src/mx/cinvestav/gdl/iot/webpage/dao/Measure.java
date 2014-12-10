package mx.cinvestav.gdl.iot.webpage.dao;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "data")
public class Measure
{
	@Id
	@GeneratedValue
	@Column(name = "iddata")
	private Integer id;
	private String measure;
	private Timestamp measure_date;
	private Integer idsensor;
	private Integer idthing;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getMeasure()
	{
		return measure;
	}

	public void setMeasure(String measure)
	{
		this.measure = measure;
	}

	public Timestamp getMeasure_date()
	{
		return measure_date;
	}

	public void setMeasure_date(Timestamp measure_date)
	{
		this.measure_date = measure_date;
	}

	public Integer getIdsensor()
	{
		return idsensor;
	}

	public void setIdsensor(Integer idsensor)
	{
		this.idsensor = idsensor;
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
