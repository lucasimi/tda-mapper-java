package org.lucasimi.tda.mapper.graph;

import java.util.Collection;

public class MapperVertex {
  
    private int[] ids;

    public MapperVertex(Collection<Integer> ids) {
        this.ids = new int[ids.size()];
        int n = 0;
        for (Integer id : ids) {
            this.ids[n] = id;
            n += 1;
        }
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public int getSize() {
        return this.ids.length;
    }

}
