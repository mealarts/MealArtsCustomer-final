package com.mealarts.Helpers;

import com.mealarts.Helpers.Utils.VendorUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChefChainedComparator implements Comparator<VendorUtils> {
 
    private List<Comparator<VendorUtils>> listComparators;
 
    @SafeVarargs
    public ChefChainedComparator(Comparator<VendorUtils>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }
 
    @Override
    public int compare(VendorUtils ven1, VendorUtils ven2) {
        for (Comparator<VendorUtils> comparator : listComparators) {
            int result = comparator.compare(ven1, ven2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}