#include<bits/stdc++.h>
using namespace std;

int main() {
    freopen ("../temp_dir/part-00000", "r", stdin);
    freopen ("key-score.txt", "w", stdout);

    vector< pair<int, string> >key_score;

    string line;
    while (getline(cin, line)){
        int score = 1;
        string word = "";
        for (int i = 0; i < line.size(); i++){
            if (line[i] == '\t')
                break;
            word.append(1u, line[i]);
        }
        for (int i = 0; i < line.size(); i++)
            if (line[i] == ' ')
                score++;
        key_score.push_back({score, word});
    }

    sort(key_score.begin(), key_score.end());
    reverse(key_score.begin(), key_score.end());

    for (int i = 0; i < key_score.size(); i++){
        cout << key_score[i].second << " " << key_score[i].first << endl;
    }

    return 0;
}
