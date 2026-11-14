package firstphase;

public class Mission {
    private int id, xpReward;
    private String title, description, category, difficulty, optionA, optionB, optionC, optionD;
    private boolean attempted;

    public int getId(){return id;} public void setId(int v){id=v;}
    public int getXpReward(){return xpReward;} public void setXpReward(int v){xpReward=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getDifficulty(){return difficulty;} public void setDifficulty(String v){difficulty=v;}
    public String getOptionA(){return optionA;} public void setOptionA(String v){optionA=v;}
    public String getOptionB(){return optionB;} public void setOptionB(String v){optionB=v;}
    public String getOptionC(){return optionC;} public void setOptionC(String v){optionC=v;}
    public String getOptionD(){return optionD;} public void setOptionD(String v){optionD=v;}
    public boolean isAttempted(){return attempted;} public void setAttempted(boolean v){attempted=v;}
}
