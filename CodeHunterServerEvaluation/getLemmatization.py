import spacy
import sys

# Cargar el modelo de idioma español
nlp = spacy.load("es_core_news_md")

# Texto en español para lematizar
texto = sys.argv[1]

# Separar el texto en palabras
palabras = texto.split()

# Lematizar cada palabra individualmente sin considerar el contexto
for palabra in palabras:
    # Crear un "doc" para cada palabra como un documento independiente
    doc = nlp.make_doc(palabra)
    # Procesar la palabra con el pipeline
    doc = nlp(doc.text)
    for token in doc:
        print(token.lemma_)
