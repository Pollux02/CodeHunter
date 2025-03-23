import java.util.ArrayList;
import java.util.List;

public class Test {
	private int id;
    private List<String> inputs;
    private List<String> outputs;
    private boolean seekOrder;

    public Test(int id) 
    {
        this.id = id;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.seekOrder = false;
    }
    
    public int getId() 
    {
        return id;
    }

    public void setId(int id) 
    {
        this.id = id;
    }

    public List<String> getInputs() 
    {
        return inputs;
    }

    public void setInputs(List<String> inputs) 
    {
        this.inputs = inputs;
    }
    
    public List<String> getOutputs() 
    {
        return outputs;
    }

    public void setOutputs(List<String> outputs) 
    {
        this.outputs = outputs;
    }
    
    public boolean getSeekOrder()
    {
    	return seekOrder;
    }
    
    public void setSeekOrder(boolean seekOrder)
    {
    	this.seekOrder = seekOrder;
    }
}
