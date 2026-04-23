import tiktoken


def count_tokens(text, tokenizer=tiktoken.get_encoding("cl100k_base")):
    tokens = tokenizer.encode(text)
    return len(tokens)


def count_tokens_in_file(input_file):
    tokenizer = tiktoken.get_encoding("cl100k_base")

    with open(input_file, 'r', encoding='utf-8') as file:
        text = file.read()

    total_tokens = count_tokens(text, tokenizer)

    return total_tokens


def curtail_text_to_tokens(text, num_tokens):
    tokenizer = tiktoken.get_encoding("cl100k_base")
    tokens = tokenizer.encode(text)

    if len(tokens) <= num_tokens:
        return text

    curtailed_tokens = tokens[:num_tokens]
    curtailed_text = tokenizer.decode(curtailed_tokens)

    return curtailed_text

'''
A function that will take a context text as input and curtail from the bottom until the number of tokens
is less than or equal to the specified num_tokens.

While the token number is greater, it will find the last occurrence of "............" and remove everything after it.

Return the curtailed text.
'''

def curtail_context_to_fixed_tokens(context_text, num_tokens):
    tokenizer = tiktoken.get_encoding("cl100k_base")
    tokens = tokenizer.encode(context_text)

    while len(tokens) > num_tokens:
        last_occurrence = context_text.rfind("............")

        if last_occurrence == -1:
            last_occurrence = context_text.rfind("\n")
            if last_occurrence == -1:
                return ""
        context_text = context_text[:last_occurrence]
        tokens = tokenizer.encode(context_text)

    return context_text

def curtail_context_to_cut_tokens(context_text, cut_tokens):
    fixed_tokens = count_tokens(context_text) - cut_tokens
    return curtail_context_to_fixed_tokens(context_text, fixed_tokens)

def curtail_text_to_fixed_tokens(text, num_tokens):
    tokenizer = tiktoken.get_encoding("cl100k_base")
    tokens = tokenizer.encode(text)

    while len(tokens) > num_tokens:
        last_occurrence = text.rfind("\n")
        if last_occurrence == -1:
            return ""
        text = text[:last_occurrence]
        tokens = tokenizer.encode(text)

    return text

def curtail_text_to_cut_tokens(text, cut_tokens):
    fixed_tokens = count_tokens(text) - cut_tokens
    return curtail_context_to_fixed_tokens(text, fixed_tokens)
