echo docker build -t "icdext:1.0"  .
docker run --rm -it   --mount type=bind,source=/home/hcadavid/RUG-local/icd-pipeline-projects/DaC-asciidoctor-extensions-baseline,target=/adocsrc icdext:1.0  /bin/bash
