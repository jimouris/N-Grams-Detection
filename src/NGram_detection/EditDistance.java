package NGram_detection;

/**
 * Created by jimouris on 11/20/16.
 */
public class EditDistance {

    private static int min(int x, int y, int z) {
        if (x < y && x <z) return x;
        if (y < x && y < z) return y;
        else return z;
    }

    public static int editDist(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();

        // Create a table to store results of subproblems
        int dp[][] = new int[m+1][n+1];

        // Fill d[][] in bottom up manner
        for (int i=0; i<=m; i++) {
            for (int j=0; j<=n; j++) {
                // If first string is empty, only option is to isnert all characters of second string
                if (i==0)
                    dp[i][j] = j;  // Min. operations = j
                    // If second string is empty, only option is to remove all characters of second string
                else if (j==0)
                    dp[i][j] = i; // Min. operations = i
                    // If last characters are same, ignore last char and recur for remaining string
                else if (str1.charAt(i-1) == str2.charAt(j-1))
                    dp[i][j] = dp[i-1][j-1];
                    // If last character are different, consider all possibilities and find minimum
                else
                    dp[i][j] = 1 + min(dp[i][j-1], dp[i-1][j], dp[i-1][j-1]); // Insert, Remove, Replace
            }
        }
        return dp[m][n];
    }

}
