FROM gbaydin/pyprob

RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt update
RUN apt install -y openjdk-8-jdk

RUN apt install -y wget

RUN mkdir /code/lein
RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -P /code/lein/
RUN chmod a+x /code/lein/lein

ENV PATH="/code/lein:${PATH}"
ENV LEIN_ROOT="true"

RUN lein
RUN cd /code && git clone -b development https://bitbucket.org/probprog/anglican.git
RUN cd /code/anglican && lein install

ARG GIT_COMMIT="unknown"
LABEL git_commit=$GIT_COMMIT

RUN mkdir /code/anglican-infcomp
COPY . /code/anglican-infcomp
RUN cd /code/anglican-infcomp && lein install

WORKDIR /workspace
RUN chmod -R a+w /workspace
CMD bash
