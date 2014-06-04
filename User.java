public class User
{
	private String ID;
	private boolean inBattle;
	
	public User(String ID)
	{
		this.ID = ID;
		this.inBattle = false;
	}
	public String getID() 
	{
		return ID;
	}
	public boolean getBattleStatus()
	{
		return inBattle;
	}
	public void switchBattleFlag()
	{
		if(inBattle == false)
			inBattle = true;
		else
			inBattle = false;
	}
}