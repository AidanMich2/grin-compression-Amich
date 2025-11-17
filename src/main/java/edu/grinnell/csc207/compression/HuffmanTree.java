package edu.grinnell.csc207.compression;

import java.util.Collection;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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

    public class Node{
        Short character;
        int charFreq;
        Node leftChild;
        Node rightChild;
        int childrenSum;
        public Node (Short character, int charFreq, Node leftChild, Node rightChild, int childrenSum){
            this.character = character;
            this.charFreq = charFreq;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.childrenSum = childrenSum;
        }

        public int compareTo (Node node){
            if (this.charFreq < node.charFreq){
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
        // Set<Map.Entry <Short, Integer>> list = freqs.entrySet();
        // PriorityQueue pq = new PriorityQueue<>();
        Short [] arr = (Short []) freqs.keySet ().toArray ();
        PriorityQueue pq = new PriorityQueue <Node>();
        for (int i = 0; i < freqs.size (); i++){
            pq.add (new Node (arr [i], freqs.get (arr [i]), null,null,0));
        }

        if (pq.size () == 0){
            return;
        }
        else if (pq.size () == 1){
            return;
        } else {
            while(pq.size() >= 2){
            for( int j = 0; j < pq.size(); j++){
                Node temp1 = (Node) pq.poll();
                Node temp2 = (Node) pq.poll();
                pq.add(new Node(null, temp1.charFreq + temp2.charFreq, temp1, temp2, 0));
            }
            }
        }
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {
        // TODO: fill me in!
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public void serialize (BitOutputStream out) {
        // TODO: fill me in!
    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }
}
