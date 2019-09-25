public class BitTreeNode<T> implements Comparable<BitTreeNode>{
    BitTreeNode left;
    BitTreeNode right;
    int frequency;
    Character character;
    Character[] order = { '~', '\n', ' ', '`', null };
    public BitTreeNode(int frequency, Character character){
        this.character = character;
        this.frequency = frequency;
    }

    public int compareTo(BitTreeNode other){
        if (frequency < other.frequency){
            return -1;
        }
        else if (frequency > other.frequency){
            return 1;
        }
        else {
            int thisPos = positionInArray(this.character);
            int otherPos = positionInArray(other.character);

            return thisPos < otherPos ? -1 : 1;
        }
    }

    private int positionInArray(Character c)
    {
        for (int i = 0; i < order.length; i++)
        {
            if (order[i] == c) {
                return i;
            }
        }

        return positionInArray('`');
    }
}
