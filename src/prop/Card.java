package prop;

public class Card {
    // Do not use setters/getters too much in our inner structure which may reduce performance.
    public String displayName;
    public int value;
    public boolean visible;
    public CardType type;
    
    public Card(String displayName, int value, boolean visible, CardType type) {
        this.displayName = displayName;
        this.value = value;
        this.visible = visible;
        this.type = type;
    }
    
    @Override
    public String toString() {
        if (visible) {
            return "[" + displayName + ", " + value + ", " + type + "]";
        } else {
            return "[invisible]";
        }
        
    }
}
