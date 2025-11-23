package edu.grinnell.csc207.compression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Optional;
import java.util.Iterator;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits.  However, we also need to encode a special EOF character to
 * denote the end of a .grin file.  Thus, we need 9 bits to store each
 * byte value.  This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {
    private Short eof = (short) 100000000;
    private static Node root;
    public class Node implements Comparable <Node>{
        private Short character;
        private int charFreq;
        private Node leftChild;
        private Node rightChild;
        public Node (Short character, int charFreq, Node leftChild, Node rightChild){
            this.character = character;
            this.charFreq = charFreq;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }


        /**
         * Acknowledgement: Used stack overflow to understand how to change what field is compared within a priority queue.
         * 
         */
        public int compareTo (Node node){
            if (charFreq< node.charFreq){
                return -1;
            }
            else if (this.charFreq == node.charFreq){
                return 0;
            }
            else{
                return 1;
            }
        }
    }

    

    // public class Priority{
    //     Short character;
    //     Integer frequency;
    //     public Priority (Short character, Integer frequency){
    //         this.character = character;
    //         this.frequency = frequency;
    //     }
    // }


    /**
     * Constructs a new HuffmanTree from a frequency map.
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree (Map<Short, Integer> freqs) {
        freqs.put(eof,1);

        Iterator <Short> iterate = freqs.keySet ().iterator();
        // Short [] arr = (Short []) freqs.keySet ().toArray ();
        PriorityQueue <Node> pq = new PriorityQueue <Node>();
        while(iterate.hasNext ()){
            Short temp = iterate.next ();
            pq.add (new Node (temp, freqs.get (temp), null,null));
        }
        
        
        while(pq.size() >= 2){
            for(int j = 0; j < pq.size(); j++){
                Node temp1 = (Node) pq.poll();
                Node temp2 = (Node) pq.poll();
                root = new Node(null, temp1.charFreq + temp2.charFreq, temp1, temp2);//internal node line
                // System.out.println (root.charFreq);
                pq.add(root);
            }
        }
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {
        int fileType = in.readBits (32);
        // while ()
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public static void serialize (BitOutputStream out) {
        System.out.println ("went into serialize");
        serializeHelper(out, root);
    }

    public static void serializeHelper (BitOutputStream out, Node cur){
        System.out.println ("went into serializeHelper");
        if (cur == null){
            return;
        }
        if(cur.leftChild != null){//internal node
            out.writeBit(1);
            serializeHelper (out, cur.leftChild);
            serializeHelper (out, cur.rightChild);
        } else {//lEAF
            out.writeBit(0);
            // System.out.println (root.character);
            out.writeBits(cur.character, 9);//figure out later whether 8 or 9 bits to write, ie. whether we need to manually add a 0 to character
        }
        
    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public static void encode (BitInputStream in, BitOutputStream out) {
        serialize (out);
        // Map<Short, String> encodedMap = new HashMap <> ();
        // encodeHelper("", encodedMap, root);
        // while (in.getDigits () >= 9){// double check logic later
        //     Short temp = (short) in.readBits (8);
        //     out.writeBits(Integer.parseInt (encodedMap.get (temp)), encodedMap.get (temp).length ());
        // }
        in.finalize ();
        out.finalize ();
        
    }

    public static void encodeHelper (String code, Map<Short,String> encodedMap, Node cur){
        if (cur == null){
            return;
        }
        
        if (cur.leftChild == null){
            System.out.println (cur.character + ": " + code);
            encodedMap.put (cur.character,code);
        }
        encodeHelper("0" + code, encodedMap, cur.leftChild);
        encodeHelper("1" + code, encodedMap, cur.rightChild);

        /**
         * if it is a leaf then add it and the current code to the map, and then do a recursive call
         * if it is not a leaf and instead a internal node, determined by having children then dont add it to the map and
         * instead go straight to the recursive call.
         * each recursive call will move either left or right and alter the code by accordingly adding 0 or 1
         * 
         * 
         */
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public static void decode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }
}
