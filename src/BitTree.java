import java.io.*;
import java.util.*;


public class BitTree {
    private String fileName;
    private String txt;
    private HashMap<Character, Integer> sequence;
    private PriorityQueue<BitTreeNode> nodes;
    private BitTreeNode finalTree;
    private HashMap<Character, String> binary;
    private Set<Character> keys;
    private String finalCompression;
    private String s;


    public BitTree(String fileName, String txt){
        sequence = new HashMap<Character, Integer>();
        nodes = new PriorityQueue<BitTreeNode>();
        binary = new HashMap<Character, String>();
        s = fileName;
        this.fileName = fileName;
        this.txt = txt;

    }
    public void encode(){
        nodes.clear();
        try {
            File f = new File("./input/" + fileName + this.txt);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);


            String c = br.readLine();
            while (c != null) {

                for (int i = 0; i < c.length(); i++) {
                    Character currentChar = c.charAt(i);

                    if (sequence.containsKey(currentChar)) {
                        int newValue = sequence.get(currentChar) + 1;
                        sequence.put(currentChar, newValue);
                    } else {
                        sequence.put(currentChar, 1);
                    }
                }

                c = br.readLine();

                if (sequence.containsKey('\n')) {
                    int newValue = sequence.get('\n') + 1;
                    sequence.put('\n', newValue);
                } else {
                    sequence.put('\n', 1);
                }
            }
            sequence.put('~', 1);

            finalTree = buildTree(sequence);

            Set<Character> keys = sequence.keySet();
            for (Character key : keys) {
                binary.put(key, findValue(finalTree, key, ""));
            }

            PrintWriter pw1 = new PrintWriter("./output/" + s + ".bwt.histoMap" + this.txt);
            pw1.write(sequenceString());
            PrintWriter pw2 = new PrintWriter("./output/" + s + ".bwt.codesMap" + this.txt);
            pw2.write(codemapString());


            fr.close();
            br.close();
            pw1.close();
            pw2.close();
        }
        catch(Exception e){
            System.out.print("g");
        }

    }

    public void compress(){

        try {
            File f = new File("./input/" + fileName + this.txt);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            finalCompression = "";
            String current = br.readLine();

            while(current != null) {
                for (int i = 0; i < current.length(); i++) {
                    finalCompression += binary.get(current.charAt(i));
                }
                finalCompression += binary.get('\n');
                current = br.readLine();

            }
            finalCompression += binary.get('~');
            PrintWriter pw1 = new PrintWriter("./output/" + s + ".bwt.bits" + txt);
            pw1.write(finalCompression);

            BitOutputStream bO = new BitOutputStream("./output/" + s + ".bwt.bits");

            int bitCount = 0;
            for(int i = 0; i < finalCompression.length(); i++){
                bO.writeBit(Integer.parseInt(finalCompression.substring(i,i+1)));
                bitCount++;
                bitCount = bitCount >= 8 ? 0 : bitCount;
            }

            for (int i = bitCount; i < 8; i++) {
                bO.writeBit(0);
            }
            pw1.close();
        }
        catch(Exception e){
            System.out.print("g");
        }
    }

    private BitTreeNode buildTree(HashMap<Character, Integer> hash){

        Set<Character> hashKeys = sequence.keySet();
        PriorityQueue<BitTreeNode> queue = new PriorityQueue<BitTreeNode>();
        for (Character key : hashKeys){
            BitTreeNode currentNode = new BitTreeNode(sequence.get(key),key);
            queue.add(currentNode);
        }


        while (queue.toArray().length > 1){

            BitTreeNode b1 = queue.remove();
            BitTreeNode b2 = queue.remove();

            int sum = b1.frequency + b2.frequency;

            BitTreeNode b = new BitTreeNode(sum,null);

            b.left = b1;
            b.right = b2;

            queue.add(b);

        }

        return queue.remove();
    }

    public void decompress(){
        try{
            File f = new File("./output/" + this.s + ".bwt.histoMap" + this.txt);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            HashMap<Character,Integer> hm = new HashMap<Character, Integer>();

            String current1 = br.readLine();
            String current2 = br.readLine();

            current1 = current1.substring(1, current1.length());
            current2 = current2.substring(0,current2.length() -1);

            if (current2 != null){
                current1 += ("\n" + current2);
            }

            String[] strings = current1.split(",");
            for (int i = 0; i < strings.length; i++){
                String[] temp = strings[i].replace(" ", "").split("=");
                if (temp[0].equals("")){
                    hm.put(' ',Integer.parseInt(temp[1]));
                }
                else if (temp[0].equals("EOF")){
                    hm.put('~',Integer.parseInt(temp[1]));
                }
                else {
                    hm.put(temp[0].charAt(0), Integer.parseInt(temp[1]));
                }
            }


            int counter = 0;
            BitTreeNode current = buildTree(hm);
            String decompression = "";
            Character found;


            while (counter < finalCompression.length()) {

                String currentDigit = finalCompression.substring(counter, counter + 1);

                if (currentDigit.equals("0")) {
                    current = current.left;
                }
                else {
                    current = current.right;
                }

                counter++;

                if (current.character != null) {
                    found = current.character;
                    if (!found.equals('~')) {
                        decompression += found.toString();
                    }
                    current = finalTree;
                    continue;
                }
            }
            PrintWriter pw1 = new PrintWriter("./output/" + s + ".bwt.decoded" + txt);
            pw1.write(decompression);
            pw1.close();
            System.out.println(decompression);
        }
        catch(Exception e){
            System.out.print("g");
        }
    }

    public double inputFileSize(){
        try {

            File f = new File("./input/" + s + txt);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String current = br.readLine();

            return current.length() + 1;
        }
        catch(Exception e){
            return 0;
        }
    }

    public double compressedFileSize() {
        try{
            File f = new File("./output/" + s + ".bwt.bits" + txt);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            int round = br.readLine().length();
            if (round % 8 != 0){
                round += (8 - (round % 8));
            }
            return round/8;
        }
        catch(Exception e){
            return 0;
        }
    }

    private String sequenceString() {
        Set<Character> key = sequence.keySet();
        String printed = "";
        printed += "{";
        int counter = 0;

        for(Character k : key) {


            // print char
            //  a. if char == '~' then print EOF
            //  b. else print char
            // print =
            // print value
            // if not last time print ", "

            if (k == '~') {
                printed += "EOF";
            }
            else {
                printed += k;
            }
            printed += "=";
            printed += sequence.get(k);
            if (counter < key.size() - 1) {
                printed += ", ";
            }
            counter++;
        }

        printed += "}";
        return printed;
    }

    private String codemapString() {
        Set<Character> keys = binary.keySet();
        String printed = "";
        printed += "{";
        int counter = 0;

        for (Character k : keys)
        {
            if (k == '~') {
                printed += "EOF";
            }
            else {
                printed += k;
            }
            printed += "=";
            printed += binary.get(k);
            if (counter < keys.size() - 1) {
                printed += ", ";
            }
            counter++;
        }

        printed += "}";
        return printed;
    }

    public double compressionRatio(){
        return (compressedFileSize() / inputFileSize());
    }
    public String findValue(BitTreeNode tree, Character value, String sequence){

        String a = null;
        String b = null;

        if (tree == null){
            return null;
        }
        if(tree.character == value){
            return sequence;
        }

        if(tree.right != null){
            a = findValue(tree.right, value,sequence + "1");
        }

        if (tree.left != null) {
            b = findValue(tree.left, value, sequence + "0");
        }

        if (a == null){
            return b;
        }

        return a;
    }
}
