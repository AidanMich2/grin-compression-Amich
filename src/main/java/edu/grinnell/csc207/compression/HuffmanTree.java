package edu.grinnell.csc207.compression;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

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
    private static Short eof = (short) 256;
    private static Node root;
    private static Node rootForDecode;
    /**
     * Node class that is used in HuffmanTree. Contains the character, that
     * characters frequency, and a left and right child which are also nodes.
     */
    public class Node implements Comparable<Node> {
        private Short character;
        private int charFreq;
        private Node leftChild;
        private Node rightChild;
        /**
         * Constructs a node.
         * @param character is the main value that is stored.
         * @param charFreq the number of times that this char is recorded.
         * @param leftChild a node to the left-below of this node.
         * @param rightChild a node to the right-below of this node.
         */
        public Node(Short character, int charFreq, Node leftChild, Node rightChild) {
            this.character = character;
            this.charFreq = charFreq;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        /**
         * Acknowledgement: Used stack overflow to understand how to change what 
         * field is compared within a priority queue. Used to use the char 
         * frequency field to compare two nodes
         * @param node is the node being compared to.
         * @return an integer representing which is greater.
         */
        public int compareTo(Node node) {
            if (charFreq < node.charFreq) {
                return -1;
            } else if (this.charFreq == node.charFreq) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * Constructs a new HuffmanTree from a frequency map.
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        freqs.put(eof, 1);
        Iterator<Short> iterate = freqs.keySet().iterator();
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        while (iterate.hasNext()) {
            Short temp = iterate.next();
            pq.add(new Node(temp, freqs.get(temp), null, null));
        }    
        while (pq.size() >= 2) {
            Node temp1 = pq.poll();
            Node temp2 = pq.poll();
            Node tempor = new Node(null, temp1.charFreq + temp2.charFreq, temp1, temp2);
            pq.add(tempor);
        } root = pq.poll();
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        //int fileType = in.readBits(32);
        rootForDecode = huffmanTreeHelper(in, rootForDecode);
    }


    /**
     * Helper function to create a new HuffmanTree from file
     * @param in is the input bit stream.
     * @param cur is the current node being tracked for recursion.
     * @return the node that we are on, eventually constructing the entire tree.
     */
    public Node huffmanTreeHelper(BitInputStream in, Node cur) {
        int current = in.readBit();
        if (current == -1) {
            return null;
            
        } else if (current == 1) {
            cur = new Node(null, 0, null, null);
            cur.leftChild = huffmanTreeHelper(in, cur.leftChild);
            cur.rightChild = huffmanTreeHelper(in, cur.rightChild);
            return cur;
        } else {
            Node leaf = new Node((short) in.readBits(9), 
                0, null, null);
            return leaf;
        }       
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public static void serialize(BitOutputStream out) {
        serializeHelper(out, root);
    }

    /**
     * Helper function that assists in serializing a stream.
     * @param out the output stream.
     * @param cur the node that is currently on, for tracking purposes.
     */
    public static void serializeHelper(BitOutputStream out, Node cur) {
        if (cur == null) {
            return;
        } else if (cur.leftChild != null) {
            out.writeBit(1);
            serializeHelper(out, cur.leftChild);
            serializeHelper(out, cur.rightChild);
        } else {
            out.writeBit(0);
            out.writeBits(cur.character, 9);
        }
        
    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public static void encode(BitInputStream in, BitOutputStream out) {
        serialize(out);
        Map<Short, String> encodedMap = new HashMap<>();
        encodeHelper("", encodedMap, root);
        while (in.getDigits() >= 9) {
            Short temp = (short) in.readBits(8);
            // for (int i = 0; i < encodedMap.get(temp).length(); i++) {
            //     out.writeBit(Character.getNumericValue(encodedMap.get(temp).charAt(i)));
            // }
            out.writeBits(Integer.parseInt(encodedMap.get(temp)), encodedMap.get(temp).length());
        }
        // for (int j = 0; j < encodedMap.get(eof).length(); j++) {
        //     out.writeBit(Character.getNumericValue(encodedMap.get(eof).charAt(j)));
        // }
        out.writeBits(Integer.parseInt(encodedMap.get(eof)), encodedMap.get(eof).length());
        in.finalize();
        out.finalize();
    }

    /**
     * Helper function for encoding a stream. if it is a leaf then add it 
     * and the current code to the map, and then do a recursive call
     * if it is not a leaf and instead a internal node, determined by having 
     * children then dont add it to the map and
     * instead go straight to the recursive call.
     * each recursive call will move either left or right and alter the code by 
     * accordingly adding 0 or 1
     * @param code the encoded string.
     * @param encodedMap the map that we constructed used in the encoding process.
     * @param cur is the current node that the function is on.
     */
    public static void encodeHelper(String code, Map<Short, String> encodedMap, Node cur) {
        if (cur == null) {
            return;
        }
        if (cur.leftChild == null) {
            encodedMap.put(cur.character, code);
        }
        encodeHelper(code + "0", encodedMap, cur.leftChild);
        encodeHelper(code + "1", encodedMap, cur.rightChild);
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public static void decode(BitInputStream in, BitOutputStream out) {
        Node currentNode = rootForDecode;
        boolean running = true;
        while (running) {
            int cur = in.readBit();
            if (cur == -1) {
                break;
            } 
            if (cur == 0) {
                currentNode = currentNode.leftChild;
            } else if (cur == 1) {
                currentNode = currentNode.rightChild;
            }
            if (currentNode.leftChild == null) {
                if (currentNode.character.equals(eof)) {
                    running = false;
                    break;
                }
                out.writeBits(currentNode.character, 8);
                currentNode = rootForDecode;
            }
        }
        out.finalize();
        in.finalize();
    }
}