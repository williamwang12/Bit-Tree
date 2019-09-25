public class BitsMain {
    public static void main(String[]args){

        BitTree b = new BitTree("docExample",".txt");
        b.encode();
        b.compress();
        b.decompress();

    }
}
