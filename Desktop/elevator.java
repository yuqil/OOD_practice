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
	
	public int level()
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
			if (request != null) {
				// Still have tasks above current floor
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
			} else {
				// all the up task starts below current level
				request = upStops.firstEntry();
				currentFloor = request.level;
			}
		}
		else if(status == Status.DOWN)
		{
			int startFloor = currLevel;
			Request request = downStops.lowerEntry(currLevel);
			if (request != null) {
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
			}	else {
				// all the down task starts above current level, go to the highest
				request = downStops.lastEntry();
				currentFloor = request.level;				
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
			if(upStops.higherEntry(currLevel) == null)
			{
				if(!noRequests(downStops))
				{
					status = Status.DOWN;
				}
				else {
					currLevel = 0; // go to floow 0;
				}
			}
		}
		else {
			if(downStops.lowerEntry(currLevel) == level)
			{
				if(!noRequests(upStops))
				{
					status = Status.up;
				} else {
					currLevel = n; // go to top floor
				}
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


	public void startElevator() {
		while (true) {

			wihle (hastasks()) {
				openGate();
				closeGate();
			}
		}
	}
}



class ProcessJobWorker implements Runnable {

	private Elevator elevator;

	ProcessJobWorker(Elevator elevator) {
		this.elevator = elevator;
	}

	@Override
	public void run() {
		/**
		 * start the elevator
		 */
		elevator.startElevator();
	}

}


class AddJobWorker implements Runnable {

	private Elevator elevator;
	private Request request;

	AddJobWorker(Elevator elevator, Request request) {
		this.elevator = elevator;
		this.request = request;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		elevator.addJob(request);
	}

}



public class TestElevator {

	public static void main(String args[]) {

		Elevator elevator = new Elevator();

		/**
		 * Thread for starting the elevator
		 */
		ProcessJobWorker processJobWorker = new ProcessJobWorker(elevator);
		Thread t2 = new Thread(processJobWorker);
		t2.start();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ExternalRequest er = new ExternalRequest(Direction.DOWN, 5);

		InternalRequest ir = new InternalRequest(0);

		Request request1 = new Request(ir, er);


		/**
		 * Pass job to the elevator
		 */
		new Thread(new AddJobWorker(elevator, request1)).start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}