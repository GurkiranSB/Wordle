import pandas as pd

def read_wordle_dictionary():
	#Read in the list of all possible words in Wordle.
	words = pd.read_csv('words/words.csv', header = None)
	words = words.squeeze()
	words.rename("Words", inplace = True)
	return(words)

def print_all_list_elements(words):
	p = '' #initialize a random value for the while loop.
	while p not in ['y', 'n']:
		p = input("\nPrint each word from this list?! (y/n): ")
		if p == 'y':
			for word in words:
				print(word)

def display_initial_word_list(words):
	print("\nWelcome to the Wordle Assistant!")
	print("Let's begin by viewing a list of all possible valid Wordle words.")
	print("\nThe length of the list of all possible words is:", len(words))
	print("\nHere are some of the words from the list:")
	print(words.head().to_string(index = False))
	print(".\n.\n.\n.\n.\n")
	print_all_list_elements(words)

def get_input():
	characters = input("\nEnter the word you have already inputted in the game: ")
	colors = input("Enter the colors the game returned for this word, green (g), yellow (y), or black (b): ")
	return(characters, colors)

def reduce_words(words, characters, colors):
	#this ensures y,g are considered before b. this helps handle repeated letters
	char_col_list = []
	for pos in range(len(characters)):
		char_col_list.append((pos, characters[pos], colors[pos]))
	char_col_list.sort(key=lambda tup: tup[2], reverse=True) 
	#this for loop will work as long as there is no repeated letter in the guess inputted
	for iterable in range(len(characters)):
		position = char_col_list[iterable][0]
		character = char_col_list[iterable][1]
		color = char_col_list[iterable][2]
		if color == 'y':
			words = words[words.str.contains(pat = character)]
			words = words[words.str[position] != character]
		if color == 'g':
			words = words[words.str.contains(pat = character)]
			words = words[words.str[position] == character]
		if color == 'b':
			'''
			for a repeated character in your guess, 
			if the previous occurrance or occurrances 
			(ordered by color ygb) of this character is/are yellow, 
			then treat this new 'black' character as a yellow
			
			for a repeated character in your guess, 
			if the previous occurrance or occurrances 
			(ordered by color ygb) of this character is/are green, 
			then treat this new 'black' character as yellow in all positions, 
			except the previous occurrances' positions.
			'''
			if character not in [char_col_list[x][1] for x in range(iterable)]:
				words = words[~words.str.contains(pat = character)]
			else:
				for x in range(iterable):
					if char_col_list[x][1] == character and char_col_list[x][2] == 'y': 
						words = words[words.str[position] != character]
					#improve this. all positions except prev positions
					if char_col_list[x][1] == character and char_col_list[x][2] == 'g': 
						words = words[words.str[position] != character]	
	return words

def display_statistics(words):
	dictionary = {}
	for ascii_value in range(97, 123):
		stat = words.str.count(chr(ascii_value)).sum()
		#stat = words[words.str.contains(pat = chr(ascii_value))].count()
		dictionary[chr(ascii_value)] = stat
	print("\nFrequency distribution for this list is:\n")
	dict_to_list = [(value, key) for key, value in dictionary.items() if value != 0]
	dict_to_list.sort(reverse =True)
	for value, key in dict_to_list:
		print(f"{key}:{value}")
	print("\n\nPro tip: Choose words which have the letters with the highest frequency in the list!")

#def suggest_next_word(words):


#Read in the list of all possible words in Wordle.
words = read_wordle_dictionary()

#Display ALL the possible words first.
display_initial_word_list(words)

'''
Moving forward, considering we will remove list 
elements, we can rename the "Name" parameter of the 
Pandas Series "words" from "Words" to "Remaining Words"
'''
words.rename("Remaining Words", inplace = True)

'''
Display some statistics of the initial list of words.
Currently we only display the frequency of each letter of the alphabet
in the entire list of words.
This should help in choosing the next guess in the game; Basing the 
next guess on the most frequently occuring letters in the remaining list of words.
'''
display_statistics(words)

continue_input = 1
while continue_input:
	characters, colors = get_input()
	print("The inputted word is: ", characters)
	print("Colors returned by the game are: ", colors)

	words = reduce_words(words, characters, colors)
	print("length of updated list = ", len(words))
	print(words.head().to_string(index = False))
	print(".\n.\n.\n.\n.")
	display_statistics(words)
	print_all_list_elements(words)

	print("\n Let's suggest the next word based on the updated list:\n")
	#suggest_next_word(words)

	continue_input = int(input("Continue? 1 for Yes, 0 to Exit!: "))
	while continue_input not in range(2):
		continue_input = int(input("Continue? 1 for Yes, 0 to Exit!: "))

