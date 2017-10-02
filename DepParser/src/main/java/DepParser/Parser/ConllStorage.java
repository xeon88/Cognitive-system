package DepParser.Parser;

import DepParser.Model.GoldTree;
import DepParser.Model.Sentence;

/**
 * Created by Marco Corona on 21/09/2017.
 */
public interface ConllStorage {
    public void addGoldTree(Sentence s, GoldTree tree);
}
