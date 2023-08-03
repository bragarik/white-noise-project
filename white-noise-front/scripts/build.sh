#!/bin/bash

# Função para exibir a animação de início da build
build_animation() {
    echo "Iniciando a build da aplicação White Noise Server..."
    echo "0%       20%       40%       60%       80%       100%"
    echo -n "█"

    for i in {1..50}; do
        sleep 0.0001
        echo -n "█"
    done

    echo ""
}

# Exibir animação de início da build
build_animation

# Comando para construir o projeto e mover/copiar para a pasta desejada
react-scripts --openssl-legacy-provider build 

rm -r "$1"
mkdir "$1"
for file in build/*; do
    mv "$file" "$1"
done

echo "
    Build da aplicação White Noise Server concluída!
    "