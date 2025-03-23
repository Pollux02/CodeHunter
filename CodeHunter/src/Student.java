import java.util.ArrayList;
import java.util.List;

public class Student {
	private String nickName;
	private String codePath;
	private int score;
	private String faults;
	private int numVersions;
	private List<Variable>variables;
	
	public Student() 
    {
        this.nickName = "";
        this.codePath = "";
        this.score = 100;
        this.faults = "";
        this.numVersions = 0;
        this.variables = new ArrayList<Variable>();
    }
	
	public Student(String nickName, String codePath, int score, String faults) 
    {
        this.nickName = nickName;
        this.codePath = codePath;
        this.score = score;
        this.faults = faults;
        this.numVersions = 0;
        this.variables = new ArrayList<Variable>();
    }
	
	public String getNickName()
	{
		return nickName;
	}
	
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
	
	public String getCodePath()
	{
		return codePath;
	}
	
	public void setCodePath(String codePath)
	{
		this.codePath = codePath;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
	
	public String getFaults()
	{
		return faults;
	}
	
	public void setFaults(String faults)
	{
		this.faults = faults;
	}
	
	public int getNumVersions()
	{
		return numVersions;
	}
	
	public void setNumVersions(int numVersions)
	{
		this.numVersions = numVersions;
	}
	
	public List<Variable> getVariables()
	{
		return variables;
	}
	
	public void setVariables(List<Variable>variables)
	{
		this.variables = variables;
	}
}
