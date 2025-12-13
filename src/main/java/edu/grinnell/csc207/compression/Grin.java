package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     * @throws IOException 
     */
    public static void decode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        HuffmanTree hf = new HuffmanTree(in);
        HuffmanTree.decode(in, out);
        in.close();
        out.close();
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws IOException 
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        BitInputStream input = new BitInputStream(file);
        Map<Short, Integer> freq = new HashMap<>();

        while (input.hasBits()) {
            short temp = (short) input.readBits(8);
            if (freq.containsKey(temp)) {
                freq.put(temp, freq.get(temp) + 1);
            } else {
                freq.put(temp, 1);
            }
        }
        input.finalize();
        return freq;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     * @throws IOException 
     */
    public static void encode(String infile, String outfile) throws IOException {
        Map<Short, Integer> freq = createFrequencyMap(infile);
        HuffmanTree hf = new HuffmanTree(freq);
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        HuffmanTree.encode(in, out);
        in.close();
        out.close();
    }

    /**
     * The entry point to the program.
     * @param args the command-line arguments.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO: fill me in!
        // System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        String in = args[1];
        String out = args[2];
        if (args[0].equals("encode")) {
            encode(in, out);
        } else if (args[0].equals("decode")) {
            decode(in, out);
        }
    }
}
