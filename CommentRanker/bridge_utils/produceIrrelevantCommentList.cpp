#include<bits/stdc++.h>
#define MAX_FRACTION 10
using namespace std;

vector<string> comments;

int main() {

    freopen("../input_dir/comments.txt", "r", stdin);

    int comment_id;
    string comment;

    while (cin >> comment_id && getline(cin, comment)){
        comments.push_back(comment);
    }

    cout << "DONEE" << endl;

    freopen ("../output_dir/part-00000", "r", stdin);
    freopen ("irrelevant_comment-score.txt", "w", stdout);

    vector< pair<int, int> >comment_scores;

    int comment_score;

    while ((cin >> comment_id >> comment_score) && comment_id != EOF){
      comment_scores.push_back({comment_score, comment_id});
    }

    sort(comment_scores.begin(), comment_scores.end());

    int threshold = comment_scores[comment_scores.size() - 1].first / MAX_FRACTION;

    cout << "STARTING " << endl;
    for (int i = 0; i < comment_scores.size(); i++) {
      if (comment_scores[i].first <= threshold)
        break;
      cout << comment_scores[i].second << ' ' << comment_scores[i].first
            << "<" << comments[comment_scores[i].second] << ">" << endl;
    }

    return 0;
}
