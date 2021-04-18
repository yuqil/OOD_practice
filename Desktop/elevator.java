// DO NOT run this code, just some code used for capturing the idea

enum Direction {
	UP, DOWN
}

enum Status {
	UP, DOWN, IDLE
}

class Request implements Comparable<Request> {
	private int level;
	
	public Request(int l)
	{
		level = l;
	}
	
	public int getLevel()
	{
		return level;
	}

	public int compareTo(Request r) {
		if (r.level == this.level) return 0;
		else if (r.level < this.level) return 1;
		else return -1;
	}
}

class ElevatorButton {
	private int level;
	private Elevator elevator;
	
	public ElevatorButton(int level, Elevator e)
	{
		this.level = level;
		this.elevator = e;
	}
	
	public void pressButton()
	{
		InternalRequest request = new InternalRequest(level);
		elevator.handleInternalRequest(request);
	}
}

class ExternalRequest extends Request{

	private Direction direction;
	
	public ExternalRequest(int l, Direction d) {
		super(l);
		// TODO Auto-generated constructor stub
		this.direction = d;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
}

class InternalRequest extends Request{

	public InternalRequest(int l) {
		super(l);
		// TODO Auto-generated constructor stub
	}
}

public class Elevator {
	
	private List<ElevatorButton> buttons;
	
	private TreeMap<int, Request> upStops;
	private TreeMap<int, Request> downStops;
	
	private int currLevel;
	private Status status;
	
	public Elevator(int n)
	{
		buttons = new ArrayList<ElevatorButton>();
		upStops = new TreeMap<int, Request>(); 
		downStops = new TreeMap<int, Request>();

		currLevel = 0;
		status = Status.IDLE;
	}
	
	public void insertButton(ElevatorButton eb)
	{
		buttons.add(eb);
	}
	
	public void handleExternalRequest(ExternalRequest r)
	{
		if(r.getDirection() == Direction.UP)
		{
			upStops.put(r.level, r);
			if(noRequests(downStops))
			{
				status = Status.UP;
			}
		}
		else 
		{
			downStops.put(r.level, r);
			if(noRequests(upStops))
			{
				status = Status.DOWN;
			}
		}
	}
	
	public void handleInternalRequest(InternalRequest r)
	{
		// check valid
		if(status == Status.UP)
		{
			if(r.getLevel() >= currLevel + 1)
			{
				upStops.put(r.level, r);
			} else {
				System.out.println("invalid");
			}
		}
		else if(status == Status.DOWN)
		{
			if(r.getLevel() <= currLevel + 1)
			{
				downStops.put(r.level, r);
			} else {
				System.out.println("invalid");
			}
		}
	}
	
	public void openGate() throws Exception
	{
		if(status == Status.UP)
		{
			int startFloor = currLevel;
			Request request = upStops.higherEntry(currLevel);
			if (startFloor < request.level()) {
				for (int i = startFloor; i <= request.level(); i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("We have reached floor -- " + i);
					currentFloor = i;
					if (upStops.higherEntry(currLevel).level < request.level) {
						request = upStops.higherEntry(currLevel);
					}
				}
				upStops.remove(request.level);
			}
		}
		else if(status == Status.DOWN)
		{
			int startFloor = currLevel;
			Request request = downStops.lowerEntry(currLevel);
			if (startFloor > request.level()) {
				for (int i = startFloor; i >= request.level(); i--) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("We have reached floor -- " + i);
					currentFloor = i;
					if (downStops.lowerEntry(currLevel).level < request.level) {
						request = downStops.lowerEntry(currLevel);
					}
				}
				downStops.remove(request.level);
			}
		}
	}
	
	public void closeGate()
	{
		if(status == Status.IDLE)
		{
			if(noRequests(downStops))
			{
				status = Status.UP;
				return;
			}
			if(noRequests(upStops))
			{
				status = Status.DOWN;
				return;
			}
		}
		else if(status == Status.UP)
		{
			if(noRequests(upStops))
			{
				if(noRequests(downStops))
				{
					status = Status.IDLE;
				}
				else status = Status.DOWN;
			}
		}
		else {
			if(noRequests(downStops))
			{
				if(noRequests(upStops))
				{
					status = Status.IDLE;
				}
				else status = Status.UP;
			}
		}
	}
	
	private boolean noRequests(TreeMap<int, Request> requests)
	{
		return requests.size() == 0;
	}
	
	public String elevatorStatusDescription()
	{	
		String description = "Currently elevator status is : " + status 
				+ ".\nCurrent level is at: " + (currLevel + 1)
				+ ".\nup stop list looks like: " + upStops
				+ ".\ndown stop list looks like:  " + downStops
				+ ".\n*****************************************\n";
		return description;
	}
}