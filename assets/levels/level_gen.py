import sqlite3

toprow = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'}
homerow = {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'}
bottomrow = {'z', 'x', 'c', 'v', 'b', 'n', 'm'}
hometop = homerow.union(toprow)
homebottom = homerow.union(bottomrow)

def contains_rows(word):
    if set(word).issubset(toprow):
        return 't'
    if set(word).issubset(homerow):
        return 'h'
    if set(word).issubset(bottomrow):
        return 'b'
    if set(word).issubset(hometop):
        return 'th'
    if set(word).issubset(homebottom):
        return 'hb'
    return 'all'

f = open('1000words.txt', 'r')
words1000 = f.readlines()

homerow_words = []
toprow_words = []
bottomrow_words = []
homebottom_words = []
hometop_words = []
all_words = []


for word in sorted(words1000, key=len):
    word = word.strip()
    rows = contains_rows(word)

    # insert into list
    if rows == 'h':
        homerow_words.append(word)
    elif rows == 't':
        toprow_words.append(word)
    elif rows == 'b':
        bottomrow_words.append(word)
    elif rows == 'hb':
        homebottom_words.append(word)
    elif rows == 'th':
        hometop_words.append(word)
    all_words.append(word)

print('homerow words:', homerow_words, len(homerow_words))
print('toprow words:', toprow_words, len(toprow_words))
print('bottomrow words:', len(bottomrow_words))
print('homebottom words:', homebottom_words, len(homebottom_words))
print('hometop words:', hometop_words, len(hometop_words))
print('all words:', len(all_words))
print(all_words[:-10])

# level ranges increase in word length
# level 1 - 2: home short
# level 3: home + bottom
# level 6 - 9: top short
# level 10 - 14: home + top
# level 15 - 20: all
# level 21 - 30: all but using top 10000 words

levels = {}
# list comprehension for readability, not for performance
levels[1] = [i for i in homerow_words if len(i) <= 3]
levels[2] = [i for i in homerow_words if len(i) > 1 and len(i) <= 4]

levels[3] = [i for i in homebottom_words if len(i) > 1 and len(i) <= 4]

levels[4] = [i for i in toprow_words if len(i) <= 4] # can include 'I'
levels[5] = [i for i in toprow_words if len(i) > 2 and len(i) <= 5]
levels[6] = [i for i in toprow_words if len(i) > 2 and len(i) <= 6]
levels[7] = [i for i in toprow_words if len(i) > 2 and len(i) <= 7]

levels[8] = [i for i in hometop_words if len(i) > 2 and len(i) <= 5]
levels[10] = [i for i in hometop_words if len(i) > 2 and len(i) <= 6]
levels[11] = [i for i in hometop_words if len(i) > 2 and len(i) <= 7]
levels[12] = [i for i in hometop_words if len(i) > 3 and len(i) <= 7]
levels[13] = [i for i in hometop_words if len(i) > 3 and len(i) <= 8]
levels[14] = [i for i in hometop_words if len(i) > 2 and len(i) <= 9]

levels[15] = [i for i in all_words if len(i) > 2 and len(i) <= 8 and len(set(i)) <= 5]
levels[16] = [i for i in all_words if len(i) > 2 and len(i) <= 9]
levels[17] = [i for i in all_words if len(i) > 2 and len(i) <= 9 and len(set(i)) <= 6]
levels[18] = [i for i in all_words if len(i) > 2 and len(i) <= 10]
levels[19] = [i for i in all_words if len(i) > 3 and len(i) <= 10]
levels[20] = [i for i in all_words if len(i) > 4 and len(i) <= 11]

f = open('10000words.txt', 'r')
words10000 = sorted([w.strip() for w in f.readlines()], key=len)
levels[21] = [i for i in words10000 if len(i) > 4 and len(i) <= 10]
levels[22] = [i for i in words10000 if len(i) > 5 and len(i) <= 11]
levels[23] = [i for i in words10000 if len(i) > 6 and len(i) <= 12]
levels[24] = [i for i in words10000 if len(i) > 6 and len(i) <= 13 and len(set(i)) >= 5]
levels[25] = [i for i in words10000 if len(i) > 7 and len(i) <= 14]
levels[26] = [i for i in words10000 if len(i) > 7 and len(i) <= 14 and len(set(i)) >= 6]
levels[27] = [i for i in words10000 if len(i) > 8 and len(i) <= 15]
levels[28] = [i for i in words10000 if len(i) > 8 and len(i) <= 15]
levels[29] = [i for i in words10000 if len(i) > 9 and len(i) <= 16]
levels[30] = [i for i in words10000 if len(i) > 10]

for level in levels:
    print(level, len(levels[level]))

con = sqlite3.connect('../../typinggame.db')
con.execute('CREATE TABLE IF NOT EXISTS levels (level_id INTEGER PRIMARY KEY, words TEXT, difficulty INTEGER, waves INTEGER)')

for level, words in levels.items():
    con.execute('INSERT INTO levels (level_id, words, difficulty, waves) VALUES (?, ?, ?, ?)', (level, ','.join(words), 1, level * 10))

con.commit()
con.close()