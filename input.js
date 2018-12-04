regExps = {
"exercise_1": /[A-Z][a-z]+/,
"exercise_2": /088[1-9]{7}/,
"exercise_3": /[^01]+/,
"exercise_4": /^[^\._0-9][a-zA-Z0-9?\.?_?]{2,17}$/,
"exercise_5": /[19][^1]+$/,
"exercise_6": /class=(\"|\')(.*)(\"|\')/
};
cssSelectors = {
"exercise_1": "item>java.class1",
"exercise_2": "different>.diffClass",
"exercise_3": "java+tag",
"exercise_4": "css>item:nth-of-type(3)",
"exercise_5": "item>tag>java:nth-child(2)",
"exercise_6": "item>item>item>item>item",
"exercise_7": "different>different#diffId2>java.diffClass:last-child",
"exercise_8": "item#someId"
};

