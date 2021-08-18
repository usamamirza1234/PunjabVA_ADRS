package ast.adrs.va.Utils;

/**
 * Created by bilalahmed on 10/11/2016.
 * bilalahmed.cs@live.com
 */

public class DModel_PaginationInfo {
    public int currIndex;
    public boolean isCompleted;

    public DModel_PaginationInfo() {
        currIndex = AppConstt.PAGINATION_START_INDEX;
        isCompleted = false;
    }
}
