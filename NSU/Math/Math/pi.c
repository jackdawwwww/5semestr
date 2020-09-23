//
//  pi.c
//  Math
//
//  Created by Gala on 22/09/2020.
//  Copyright © 2020 Khlimankova Galina. All rights reserved.
//
#include <sys/types.h>
#include <sys/ddi.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <strings.h>

#define numOfIterations 20000000

struct data {
    int begin, end;
    double val;
};

void* threadBody(void* param) {
    struct data* datas = (struct data*)param;
    
    for(int i = datas->begin; i < datas->end; i++) {
        datas->val += 1.0/(i * 4.0 + 1.0);
        datas->val -= 1.0/(i * 4.0 + 3.0);
    }
}

void dataInit(pthread_t** threads, struct data ** datas, int numOfThreads) {
    *threads = (pthread_t*)malloc(sizeof(pthread_t) * numOfThreads);
    if(*threads == NULL) {
        printf("Error while allocating memory\n");
        exit(1);
    }
    
    *datas = (struct data*)malloc(sizeof(struct data) * numOfThreads);
    if(*datas == NULL) {
        printf("Error while allocating memory\n");
        free(threads);
        exit(1);
    }
    
    return;
}

void dataDelete(pthread_t** threads, struct data ** datas) {
    free(*threads);
    free(*datas);
    *threads = NULL;
    *datas = NULL;
}

void fillDatas(struct data* datas, int numOfThreads) {
    int coef = numOfIterations/numOfThreads;
    
    for (int i = 0; i < numOfThreads; i++) {
        datas[i].val = 0;
        datas[i].begin = coef * i;
        datas[i].end = coef * (i + 1);
    }
    
    datas[numOfThreads - 1].end = numOfIterations;
    return;
}


void termitateThreads(pthread_t* threads, int num) {
    for(int i = 0; i < num; i++) {
        if(pthread_cancel(threads[i])) {
            printf("Error cancelling thread");
            continue;
        }
        
        if(pthread_join(threads[i], NULL)) {
            printf("Error joining thread");
            continue;
        }
    }
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        printf("%s", "There is no num of threads\n");
        return 1;
    }

    int numOfThreads = atoi(argv[1]);
    printf("num of threads = %d\n", numOfThreads);
    
    pthread_t* threads;
    struct data * datas;
    
    dataInit(&threads, &datas, numOfThreads);
    fillDatas(datas, numOfThreads);
    
    for (int i = 0; i < numOfThreads; i++) {
        if(pthread_create(&threads[i], NULL, threadBody, (void*)(&datas[i]))) {
            printf("Thread create error");
            termitateThreads(threads, i);
            dataDelete(&threads, &datas);
            return 1;
        }
    }
    
    double pi = 0;
    for(int i = 0; i < numOfThreads; i++) {
        if(pthread_join(threads[i], NULL)) {
            printf("Joing thread error");
            termitateThreads(threads, i);
            dataDelete(&threads, &datas);
            return(1);
        }
        
        pi += datas[i].val;
    }
    
    pi *= 4.0;
    printf("pi done - %.15g \n", pi);
    
    dataDelete(&threads, &datas);
    return 0;
}
