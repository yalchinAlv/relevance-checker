#include<bits/stdc++.h>
using namespace std;

const int MAX_COMMENT_NO = 100000;

unordered_map<int, string>comments;
vector< pair<int, int> >comment_scores;
bool used[MAX_COMMENT_NO];

int main() {

    freopen("../input_dir/comments.txt", "r", stdin);

    string comment;
    int comment_id;

    while (cin >> comment_id && getline(cin, comment)){
        comments[comment_id] = comment;
    }

    cin.clear();

    freopen ("../output_dir/part-00000", "r", stdin);
    freopen ("irrelevant_comment-score.txt", "w", stdout);

    int comment_score;
    int sum = 0;

    while ((cin >> comment_id >> comment_score) && comment_id != EOF){
      comment_scores.push_back({comment_score, comment_id});
      sum += comment_score;
    }

    for (int i = 0; i < MAX_COMMENT_NO; i++)
      used[i] = false;
    for (int i = 0; i < comment_scores.size(); i++)
      used[comment_scores[i].second] = true;

    unordered_map<int, string>::iterator it;
    for (it = comments.begin(); it != comments.end(); it++)
      if (!used[it->first])
        comment_scores.push_back({0, it->first});

    sort(comment_scores.begin(), comment_scores.end());

    int threshold = sum / comment_scores.size();

    for (int i = 0; i < comment_scores.size(); i++) {
      if (comment_scores[i].first >= threshold)
        break;
      cout << comment_scores[i].second << ' ' << comment_scores[i].first << " <" << comments[comment_scores[i].second].substr(1) << ">" << endl;
    }

    freopen ("commentsSortedByRelevance.txt", "w", stdout);

    for (int i = 0; i < comment_scores.size(); i++) {
      cout << comment_scores[i].second << ' ' << comment_scores[i].first << " <" << comments[comment_scores[i].second].substr(1) << ">" << endl;
    }

    return 0;
}
